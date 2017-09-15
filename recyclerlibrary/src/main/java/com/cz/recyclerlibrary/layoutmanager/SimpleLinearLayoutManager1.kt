package com.cz.sample.ui.layoutmanager

import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.base.BaseLayoutManager

/**
 * Created by cz on 2017/9/14.
 */
open class SimpleLinearLayoutManager1 : RecyclerView.LayoutManager {
    companion object {
        val LAYOUT_START = -1
        val LAYOUT_END = 1
        val ITEM_DIRECTION_HEAD = -1
        val ITEM_DIRECTION_TAIL = 1
    }
    protected lateinit var orientationHelper: OrientationHelper
    private val layoutState=LayoutState()
    /**
     * 当前排版方向
     * @see {@link .HORIZONTAL} or {@link .VERTICAL}
     */
    private var orientation: Int = BaseLayoutManager.VERTICAL

    @JvmOverloads constructor(orientation: Int = BaseLayoutManager.VERTICAL) {
        setOrientation(orientation)
    }

    override fun generateDefaultLayoutParams() =RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    /**
     * 设置方向
     * @param orientation
     */
    open fun setOrientation(orientation: Int) {
        if (orientation != BaseLayoutManager.HORIZONTAL && orientation != BaseLayoutManager.VERTICAL) {
            throw IllegalArgumentException("invalid orientation:" + orientation)
        }
        this.orientation = orientation
        if (OrientationHelper.HORIZONTAL == orientation) {
            orientationHelper = OrientationHelper.createHorizontalHelper(this)
        } else if (OrientationHelper.VERTICAL == orientation) {
            orientationHelper = OrientationHelper.createVerticalHelper(this)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if(0==itemCount||state.isPreLayout){
            //将当前所有的RecyclerView的ChildView进行回收
            detachAndScrapAttachedViews(recycler)
        } else if(state.didStructureChange()){
            detachAndScrapAttachedViews(recycler)
            //当前有效空间
            updateLayoutStateToFillEnd(0,0)
            //填充控件
            fill(recycler,state)
        }
    }

    private fun updateLayoutStateToFillEnd(itemPosition: Int, offset: Int) {
        layoutState.layoutOffset = offset
        layoutState.available = height - paddingBottom - offset
        layoutState.position = itemPosition
        layoutState.layoutDirection = LAYOUT_END
        layoutState.itemDirection = ITEM_DIRECTION_TAIL
    }

    /**
     * 填充控件
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State):Int {
        //铺满过程中,检测并回收控件
        recycleByLayoutState(recycler)
        //当前可填充空间
        val start=layoutState.available
//        if(0>layoutState.available){
//            layoutState.scrollOffset+=layoutState.available
//        }
        var remainingSpace=layoutState.available
        while(0<remainingSpace&&hasMore(state)){
            //循环排版子控件,直到塞满为止
            val consumed=layoutChildView(recycler,state)
            layoutState.layoutOffset +=consumed*layoutState.layoutDirection
            layoutState.available-=consumed
            remainingSpace-=consumed
        }
        //返回排版后,所占用空间
        return start-layoutState.available
    }

    /**
     * 此处实现纵向滚动代码
     */
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dy == 0) {
            return 0
        }
        val layoutDirection=if(0> dy) LAYOUT_START else LAYOUT_END
        val absDy = Math.abs(dy)
        //动态更新布局状态
        updateLayoutState(layoutDirection,absDy)
        //填充当前布局
        var consumed=layoutState.scrollOffset+fill(recycler,state)
        //做边界处理,
        val scrolled = if (absDy > consumed) layoutDirection * consumed else dy
        //纵向滚动
        offsetChildrenVertical(-scrolled)
        return scrolled
    }

    /**
     * 根据滚动偏移量,更新布局状态值
     */
    private fun updateLayoutState(layoutDirection:Int,requiredSpace: Int) {
        if(layoutDirection== LAYOUT_START){
            val view=getChildAt(0)
            layoutState.layoutDirection= LAYOUT_START
            layoutState.itemDirection=ITEM_DIRECTION_HEAD
            layoutState.position=getPosition(view) + layoutState.itemDirection
            layoutState.layoutOffset =orientationHelper.getDecoratedStart(view)
            //当前RecyclerView-控件顶点 因为控件可能超出顶端
            layoutState.scrollOffset=orientationHelper.startAfterPadding-layoutState.layoutOffset
        } else if(layoutDirection== LAYOUT_END){
            val view=getChildAt(childCount-1)
            layoutState.layoutDirection= LAYOUT_END
            layoutState.itemDirection= ITEM_DIRECTION_TAIL
            layoutState.position=getPosition(view) + layoutState.itemDirection
            layoutState.layoutOffset = orientationHelper.getDecoratedEnd(view)
            //当前RecyclerView底部-最后一个控件底部,因为最后一个控件会超出底部
            layoutState.scrollOffset=layoutState.layoutOffset-orientationHelper.endAfterPadding
        }
        //记录控件,缺多少值可以排版.也可以理解为,可排版空余值,默认不可排版时,一般为-1数
        //比如往下拉,requiredSpace为10 scrollOffset为105 则当前再滑动95下一个条目才开始加载,反之亦然
        layoutState.available= requiredSpace -layoutState.scrollOffset
        debugLog("offset:" + layoutState.scrollOffset + " currentPosition = " + layoutState.position + " available:" + layoutState.available)
    }




    /**
     * 填充子控件
     */
    protected open fun layoutChildView(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val view=nextView(recycler,state)
        if (layoutState.layoutDirection == LAYOUT_END) {
            addView(view)
        } else {
            addView(view, 0)
        }
        //测量控件
        measureChildWithMargins(view,0,0)
        /* orientationHelper.getDecoratedMeasurement(view)会获取当前方向控件计算尺寸
           如horizontal 取width作为计算长度+ insets:分隔线空间+控件margin值 为总计算高度 */
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        //width+分隔线+左右margin,控制右排版位置
        val right = orientationHelper.getDecoratedMeasurementInOther(view)
        layoutDecorated(view,paddingLeft,layoutState.layoutOffset,right,layoutState.layoutOffset +consumed)
        //返回控件高度/宽
        return consumed
    }

    /**
     * 根据滑动状态,回收控件
     */
    private fun recycleByLayoutState(recycler: RecyclerView.Recycler) {
        if(layoutState.layoutDirection== LAYOUT_START){
            //回收底部控件
            recycleViewsFromEnd(recycler, layoutState.scrollOffset)
        } else if(layoutState.layoutDirection== LAYOUT_END){
            //回收顶部控件
            recycleViewsFromStart(recycler, layoutState.scrollOffset)
        }
    }

    private fun recycleViewsFromStart(recycler: RecyclerView.Recycler, dt: Int) {
        if (dt < 0) {
            return
        }
        val limit = dt
        val childCount = childCount
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            if (orientationHelper.getDecoratedEnd(child) > limit) {// stop here
                recycleChildren(recycler, 0, i)
                return
            }
        }
    }

    private fun recycleViewsFromEnd(recycler: RecyclerView.Recycler, dt: Int) {
        val childCount = childCount
        if (dt < 0) {
            return
        }
        val limit = orientationHelper.end - dt
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (orientationHelper.getDecoratedStart(child) < limit) {// stop here
                recycleChildren(recycler, childCount - 1, i)
                return
            }
        }
    }

    private fun recycleChildren(recycler: RecyclerView.Recycler, startIndex: Int, endIndex: Int) {
        if (startIndex == endIndex) {
            return
        }
        debugLog("recycleChildren:$startIndex $endIndex")
        if (endIndex > startIndex) {
            for (i in endIndex - 1 downTo startIndex) {
                removeAndRecycleViewAt(i, recycler)
            }
        } else {
            for (i in startIndex downTo endIndex + 1) {
                removeAndRecycleViewAt(i, recycler)
            }
        }
    }

    protected fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        //获取一个控件,会走完缓存->onCreateViewHolder()
        debugLog("nextView:${layoutState.position}")
        val view=recycler.getViewForPosition(layoutState.position)
        layoutState.position+=layoutState.itemDirection
        return view
    }

    protected fun hasMore(state: RecyclerView.State):Boolean{
        return layoutState.position in 0..state.itemCount-1
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


    inner class LayoutState{
        /**
         * 当前有效空间
         */
        var available=0
        /**
         * 当前排版位置
         */
        var layoutOffset =0
        /**
         * 当前滚动位置
         */
        var scrollOffset=0
        /**
         * 当前位置
         */
        var position:Int=0
        /**
         * 当前布局滑动方向
         */
        var layoutDirection=0
        /**
         * 当前条目方向
         */
        var itemDirection=0

        override fun toString(): String {
            return "available:$available scrollOffset:$scrollOffset position:$position layoutDirection:$layoutDirection"
        }
    }


}