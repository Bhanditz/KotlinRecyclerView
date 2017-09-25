package com.cz.recyclerlibrary.layoutmanager.base

import android.graphics.PointF
import android.support.annotation.FloatRange
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager

/**
 * Created by cz on 2017/9/20.
 * 完成LayoutManage的
 * 1:起始居中排版,
 * 2:滚动居中
 * 3:自动滚动居中等逻辑
 */
open class CenterLinearLayoutManager(orientation: Int=BaseLinearLayoutManager.VERTICAL) : BaseLinearLayoutManager(orientation) {
    private var minCycleCount=1//最小循环个数
    private var minScrollOffset:Float=0f
    private var listener: OnSelectPositionChangedListener?=null
    private var centerSmoothScroller: CenterSmoothScroller? = null
    var cycle=false
        set(value) {
            field=value
            removeAllViews()
            requestLayout()
        }

    override fun onAttachedToWindow(recyclerView: RecyclerView) {
        super.onAttachedToWindow(recyclerView)
        //设置滑动完成居中
        val scrollListener=CenterLinearScrollListener(this)
        scrollListener.setOnSelectPositionChangedListener(object :OnSelectPositionChangedListener{
            override fun onSelectPositionChanged(view: View?, position: Int, lastPosition: Int) {
                listener?.onSelectPositionChanged(view,position,lastPosition)
            }
        })
        recyclerView.addOnScrollListener(scrollListener)
    }

    /**
     * 设置最小的滑动偏移量
     */
    fun setMinScrollOffset(@FloatRange(from=0.0,to = 1.0) scrollOffset:Float){
        this.minScrollOffset=scrollOffset
    }

    /**
     * 最小循环个数
     */
    fun setCycleCount(minCycleCount:Int){
        this.minCycleCount=minCycleCount
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (0 < itemCount) {
            //因快速滑动过程结束后,控件的属性变化并不统一,这里强制统一
            (0..childCount - 1).map { getChildAt(it) }.forEach(this::viewScrollOffset)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        //排版完后,向上检测1次,以自动铺完中间距离顶部空间
        if(0<childCount){
            updateLayoutState(DIRECTION_START,0)
            fill(recycler,state)
        }
    }

    override fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //当前可填充空间
        val start=layoutState.available
        //为避免回收时,scrollingOffset异常
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available
        }
        //铺满过程中,检测并回收控件
        recycleByLayoutState(recycler)
        var remainingSpace=layoutState.available
        while(0<remainingSpace&&hasMore(state)){
            //循环排版子控件,直到塞满为止,
            val view = nextView(recycler,state)
            if(layoutState.layoutChildren){
//                如果是初次排版,需要将控件排到中间
                val totalSpace=orientationHelper.totalSpace
                layoutState.available=(totalSpace+orientationHelper.getDecoratedMeasurement(view))/2
                layoutState.layoutOffset =totalSpace-layoutState.available
                //重新给定可排版空间
                remainingSpace=layoutState.available
                //初始化排版
                layoutState.layoutChildren=false
            }
            //添加并测量控件
            addAdapterView(view)
            val consumed= layoutChildView(view,recycler,state)
            layoutState.layoutOffset +=consumed*layoutState.itemDirection
            layoutState.available-=consumed
            remainingSpace-=consumed
        }
        //取中心点,作滑动动作速率回调
        (0..childCount - 1).map { getChildAt(it) }.forEach(this::viewScrollOffset)
        //返回排版后,所占用空间
        return start-layoutState.available
    }

    override fun scrollOver(layoutDirection: Int, consumed: Int,dy: Int): Int {
        var dy=dy
        val absDy=Math.abs(dy)
        if(DIRECTION_START==layoutDirection){
            val view=getChildAt(0)
            val start=orientationHelper.getDecoratedStart(view)
            //向上越界
            val startAvailable=(orientationHelper.totalSpace-orientationHelper.getDecoratedMeasurement(view))/2
            if((startAvailable-start)<absDy){
                dy=start-startAvailable
            }
            debugLog("scrollOver:$start dy:$dy startAvailable:$startAvailable")
        } else {
            val view=getChildAt(childCount-1)
            val end=orientationHelper.getDecoratedEnd(view)
            //向上越界
            val endAvailable=(orientationHelper.totalSpace+orientationHelper.getDecoratedMeasurement(view))/2
            if((end-endAvailable)<absDy){
                dy=end-endAvailable
            }
            debugLog("scrollOver:$end dy:$dy endAvailable:$endAvailable")
        }
        return dy
    }

    /**
     * 当控件滑动位置发生偏移,使用此方法需要用户Adapter实现[ViewScrollOffsetCallback]
     * 在此方法中,可完成具体控件的动画控制
     * @param childView
     */
    protected open fun viewScrollOffset(childView: View) {
        val center = orientationHelper.end / 2
        var childCenter = orientationHelper.getDecoratedStart(childView) + orientationHelper.getDecoratedMeasurement(childView) / 2
        var offset:Float
        var minScroll:Float
        if(childCenter<center){
            offset = -(1f + (childCenter - center)*1f / center)//上
            offset=Math.min(0f,offset)//避免越界值
            minScroll=-minScrollOffset+(1f-minScrollOffset)*offset
        } else {
            offset = 1f - (childCenter - center)*1f / center//下
            offset=Math.max(0f,offset)//避免越界值
            minScroll=minScrollOffset+(1f-minScrollOffset)*offset
        }
        //计算缩放比例
        //动态设定adapter字体样式
        val parent = childView.parent
        if (null != parent) {
            val recyclerView = childView.parent as RecyclerView
            val adapter = recyclerView.adapter
            if (null != adapter) {
                //这里可能有两种可能,一层为,当前adapter直接使用本框架内的装饰设计模式嵌套整个Adapter
                //另一层则为直接使用原生Adapter,若为装饰设计模式,需要手动判断并处理
                if(adapter is ViewScrollOffsetCallback){
                    adapter.onViewScrollOffset(childView, getPosition(childView), findCurrentItemPosition(), offset,minScroll)
                } else if(adapter is DynamicAdapter){
                    val innerAdapter=adapter.adapter
                    if(null!=innerAdapter&&innerAdapter is ViewScrollOffsetCallback){
                        innerAdapter.onViewScrollOffset(childView, getPosition(childView), findCurrentItemPosition(), offset,minScroll)
                    }
                }
            }
        }
    }

    val currentItemPositionOffset: Int
        get() {
            val child = findOneVisibleChild(0, childCount)
            var offset = 0
            if (null != child) {
                offset = orientationHelper.getDecoratedStart(child) + orientationHelper.getDecoratedMeasurement(child) / 2 - orientationHelper.end / 2
            }
            return offset
        }

    /**
     * 复写系统自动滚动到一个居中位置
     */
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
//        super.smoothScrollToPosition(recyclerView, state, position)
        //复写 smoothLinearScroller 修改滚动时间,这里不能像系统 LinearLayoutManager 一样始终返回新的对象.会导致 LayoutManager#isSmoothScrolling失效
        //检测 position 是否在当前屏内
        val itemCount=state.itemCount
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        //代表循环模式下,当前滚动到一个上下交界处
        if(firstVisibleItemPosition>lastVisibleItemPosition){
            if(position in firstVisibleItemPosition..itemCount||position in 0..lastVisibleItemPosition){
                //包含在上段位/下段位
                smoothScrollToScreenPosition(recyclerView,position)
            } else {
                //处在屏幕外
                smoothScrollToOutPosition(recyclerView,position)
            }
        } else if (position in firstVisibleItemPosition..lastVisibleItemPosition) {
            //直接移动,当控件为最后1屏时.控件高度位置并不准确,所以采用直接的index 计算
            smoothScrollToScreenPosition(recyclerView,position)
        } else {
            //复写 smoothLinearScroller 修改滚动时间,这里不能像系统 LinearLayoutManager 一样始终返回新的对象.会导致 LayoutManager#isSmoothScrolling失效
            smoothScrollToOutPosition(recyclerView,position)
        }
    }

    /**
     * 滚动到屏幕内
     */
    protected open fun smoothScrollToScreenPosition(recyclerView: RecyclerView, position:Int){
        val childView = findViewByPosition(position)
        smoothScrollToView(recyclerView,childView)
    }

    protected open fun smoothScrollToView(recyclerView: RecyclerView,childView:View){
        val offset = orientationHelper.getDecoratedStart(childView) + orientationHelper.getDecoratedMeasurement(childView) / 2 - orientationHelper.end / 2
        if (canScrollHorizontally()) {
            recyclerView.smoothScrollBy(offset, 0)
        } else if (canScrollVertically()) {
            recyclerView.smoothScrollBy(0, offset)
        }
    }

    /**
     * 滚动到屏幕外
     */
    protected open fun smoothScrollToOutPosition(recyclerView: RecyclerView, position:Int){
        if (null == centerSmoothScroller) {
            centerSmoothScroller = object : CenterSmoothScroller(recyclerView.context) {
                override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                    return smoothScrollComputeScrollVectorForPosition(targetPosition)
                }
            }
        }
        centerSmoothScroller?.targetPosition = position
        startSmoothScroll(centerSmoothScroller)
    }

    fun smoothScrollComputeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val direction:Int
        if(cycle){
            //如果是循环模式,取最近位置方向
            direction=if(findCurrentItemPosition()<itemCount/2) DIRECTION_START else DIRECTION_END
        } else {
            //非循环方向,根据当前位置与目标取方向
            direction = if (targetPosition < findFirstVisibleItemPosition()) DIRECTION_START else DIRECTION_END
        }
        val point:PointF
        if(orientation== HORIZONTAL){
            point=PointF(direction.toFloat(), 0f)
        } else {
            point=PointF(0f, direction.toFloat())
        }
        return point
    }

    /**
     * cycle模式下,复写hasMore,默认返回true
     */
    override fun hasMore(state: RecyclerView.State): Boolean{
        //这里约束最小循环个数
        return if(cycle&&minCycleCount<itemCount) true else super.hasMore(state)
    }

    /**
     * cycle模式下,使控件一直返回
     */
    override fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        var view:View
        //计算无限循环
        if(1==itemCount){
            //兼容个数为1个的情况
            view=recycler.getViewForPosition(itemCount-1)
        } else if(cycle&&0>layoutState.position){
            view=recycler.getViewForPosition(itemCount-Math.abs(layoutState.position)%itemCount)
        } else {
            view=recycler.getViewForPosition(layoutState.position%state.itemCount)
        }
        measureChildWithMargins(view,0,0)
        layoutState.position+=layoutState.itemDirection

        view.setOnClickListener { v -> smoothScrollToView(recyclerView,v) }
        return view
    }

    fun setOnSelectPositionChangedListener(listener: OnSelectPositionChangedListener) {
        this.listener = listener
    }

}