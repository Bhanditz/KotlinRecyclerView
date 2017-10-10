package com.cz.recyclerlibrary.layoutmanager.table

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.callback.OnItemLongClickListener
import com.cz.recyclerlibrary.callback.OnTableItemClickListener
import com.cz.recyclerlibrary.debugLog
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager


/**
 * Created by cz on 2017/1/20.
 * 一个支持横向内容超出的列表,应用场景为展示数据库界面,以及事件列表
 * 因特殊的布局控制,所有外层必须 使用[TableColumnLayout]
 * @see {@link TableColumnLayout} 为一个支持横向向前排序的线性容器,而 linearLayout 最大尺寸为屏幕宽,所以无法支持此超出屏table设计
 * 此做法存在一些性能问题,如屏幕外一口气会加载过多控件.但设计上更符合如数据库这类设计.
 */
class TableLayoutManager : BaseLinearLayoutManager {
    companion object {
        const val AUTO=0x00
        const val FIX=0x01
    }
    private var onItemClickListener:OnTableItemClickListener?=null
    private var onItemLongClickListener:OnItemLongClickListener?=null
    private lateinit var headerSizeArray:IntArray
    private var headerView:TableColumnLayout?=null
    private var headerBackgroundDrawable:Drawable?=null
    private val headerItemPadding=ItemPadding()
    private var itemBackgroundDrawable:Drawable?=null
    private val itemPadding=ItemPadding()
    private var headerFullMode= AUTO
    private var headerMinWidth =0f
    private var headerMaxWidth =0f
    private var totalWidth: Int = 0
    private var strokeWidth: Float = 0f
    private var drawable: Drawable? = null
    private var scrollX=0
    constructor() : super(BaseLinearLayoutManager.VERTICAL)

    /**
     * 设置条目列最小宽度
     */
    fun setHeaderMinWidth(minWidth: Float) {
        this.headerMinWidth =minWidth
    }

    /**
     * 设置条目列最大宽度
     */
    fun setHeaderMaxWidth(maxWidth: Float) {
        this.headerMaxWidth =maxWidth
    }


    fun setDrawable(drawable: Drawable?) {
        this.drawable = drawable
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
    }

    fun setItemBackground(drawable: Drawable?) {
        itemBackgroundDrawable=drawable
    }

    fun setItemPadding(padding: Int) {
        itemPadding.padding=padding
    }

    fun setItemPaddingLeft(padding: Int) {
        itemPadding.left=padding
    }

    fun setItemPaddingTop(padding: Int) {
        itemPadding.top=padding
    }

    fun setItemPaddingRight(padding: Int) {
        itemPadding.right=padding
    }

    fun setItemPaddingBottom(padding: Int) {
        itemPadding.bottom=padding
    }

    fun setHeaderFullMode(mode:Int){
        this.headerFullMode= mode
    }

    fun setHeaderBackground(drawable: Drawable?) {
        headerBackgroundDrawable=drawable
    }

    fun setHeaderPadding(padding: Int) {
        headerItemPadding.padding=padding
    }

    fun  setHeaderPaddingLeft(padding: Int) {
        headerItemPadding.left=padding
    }

    fun setHeaderPaddingTop(padding: Int) {
        headerItemPadding.top=padding
    }

    fun setHeaderPaddingRight(padding: Int) {
        headerItemPadding.right=padding
    }

    fun setHeaderPaddingBottom(padding: Int) {
        headerItemPadding.bottom=padding
    }

    override fun onAttachedToWindow(recyclerView: RecyclerView) {
        super.onAttachedToWindow(recyclerView)
        recyclerView.addItemDecoration(object :RecyclerView.ItemDecoration(){
            override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
                super.onDraw(canvas, parent, state)
                if(parent is TableView){
                    //绘背景
                    canvas.save()
                    canvas.translate(scrollX*1f,parent.originalPaddingTop*1f)
                    headerView?.draw(canvas)
                    canvas.restore()
                }
            }
        },-1)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        //解决首次排版header后,paddingTop重设导致的底部排版问题.待更优解决方法
        if(0<childCount){
            updateLayoutState(DIRECTION_END,0)
            fill(recycler,state)
        }
    }


    /**
     * 此处复写,完成第一次排版时,计算每一列宽度问题
     */
    override fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //为避免回收时,scrollingOffset异常
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available
        }
        //铺满过程中,检测并回收控件
        recycleByLayoutState(recycler)
        //当前可填充空间
        val start=layoutState.available
        var remainingSpace=layoutState.available
        while(0<remainingSpace&&hasMore(state)){
            //循环排版子控件,直到塞满为止,
            val view = nextView(recycler,state)
            if(view is TableColumnLayout){
                if(layoutState.layoutChildren){
                    val adapter=recyclerView.adapter as TableAdapter<*>
                    val headerCount= adapter.getColumnCount()
                    //初始化排版
                    totalWidth = getDecoratedMeasuredWidth(view)
                    //设定头排列模式,自动,以及固定
                    val totalSpace=width-paddingLeft-paddingRight
                    //条目剩余填充空间
                    val itemSpace=when(headerFullMode){
                        AUTO->if(totalSpace>totalWidth) (totalSpace-totalWidth)/headerCount else 0
                        else->0
                    }
                    //获得每个条目长度
                    var array= (0..view.childCount - 1).map { index->
                        val childView=view.getChildAt(index)
                        val size=Math.min(headerMaxWidth.toInt(),Math.max(headerMinWidth.toInt(),childView.measuredWidth))+itemSpace
                        //返回最终adapter返回的size,此处可用于adapter做一些修改
                        adapter.getHeaderSize(index,size)
                    }
                    headerSizeArray =array.toIntArray()
//                    //初始化并排版headerView
                    layoutHeaderView(view, headerSizeArray,adapter,headerCount)
                    //从此位置开始排版
                    layoutState.layoutOffset = paddingTop
                    //记录横向滚动起始
                    scrollX=paddingLeft
                    //标记为己排版
                    layoutState.layoutChildren=false
                }
                view.setDividerSize(strokeWidth)
                view.setDividerDrawable(drawable)
                view.setColumnSize(headerSizeArray)
                //再次测试,改变大小
                measureChildWithMargins(view,0,0)
            }
            //添加控件
            addAdapterView(view)
            val consumed= layoutChildView(view,recycler,state)
            layoutState.layoutOffset +=consumed*layoutState.itemDirection
            layoutState.available-=consumed
            remainingSpace-=consumed
        }
        //返回排版后,所占用空间
        return start-remainingSpace
    }

    /**
     * 排版HeaderView
     */
    private fun layoutHeaderView(childView:View,columnArray:IntArray,adapter:TableAdapter<*>,headerCount:Int){
        val tableView=recyclerView as? TableView?:return
        val layout=TableColumnLayout(recyclerView.context)
        layout.setColumnSize(columnArray)
        for (index in (0..headerCount - 1)) {
            //获取headerItem
            val headerItemView=adapter.getHeaderItemView(layout,index)
            layout.addView(headerItemView)
            //绑定header数据
            adapter.onBindHeaderItemView(layout,headerItemView,index)
        }
        //绑定headerView
        adapter.onBindHeaderView(layout)
        //模仿测量
        val widthSpec = View.MeasureSpec.makeMeasureSpec(childView.measuredWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(childView.measuredHeight, View.MeasureSpec.AT_MOST)
        val headerWidth=ViewGroup.getChildMeasureSpec(widthSpec,paddingLeft+paddingRight,childView.measuredWidth)
        val headerHeight=ViewGroup.getChildMeasureSpec(heightSpec,paddingTop+paddingBottom,childView.measuredHeight)
        layout.measure(headerWidth,headerHeight)
        //模仿排版
        layout.layout(0,0,layout.measuredWidth,layout.measuredHeight)
        //初始化属性
        layout.setPadding(itemPadding.left, itemPadding.top,itemPadding.right,itemPadding.bottom)
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(headerBackgroundDrawable?.constantState?.newDrawable())
        } else {
            layout.background=headerBackgroundDrawable?.constantState?.newDrawable()
        }
        headerView=layout
        //空出header空间
        tableView.setOriginalPadding(paddingLeft, tableView.originalPaddingTop+layout.measuredHeight, paddingRight, paddingBottom)
    }

    override fun canScrollHorizontally(): Boolean {
        return totalWidth>width
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (0 == itemCount||0==dx) {
            return 0
        }
        val childView = getChildAt(0)
        //横向最大滚动距离
        val decoratedLeft = getDecoratedLeft(childView)
        val decoratedRight = getDecoratedRight(childView) - width
        val layoutDirection = if (0 > dx) DIRECTION_START else DIRECTION_END
        var scrolled = dx
        if (DIRECTION_START == layoutDirection) {
            //to left
            if(decoratedLeft>paddingLeft){
                scrolled=0
            } else if (decoratedLeft-dx > paddingLeft) {
                scrolled = decoratedLeft-paddingLeft
            }
        } else if (DIRECTION_END == layoutDirection) {
            //to right
            if (0 > decoratedRight) {
                scrolled = 0
            } else if (decoratedRight - dx < 0) {
                scrolled = decoratedRight
            }
        }
        //记录横向滚动值
        scrollX=decoratedLeft-scrolled
        //横向滚动
        offsetChildrenHorizontal(-scrolled)
        debugLog("scrollHorizontallyBy:$dx scrollX:$scrollX Direction:$layoutDirection scrolled:$scrolled Left:$decoratedLeft Right:$decoratedRight")
        return scrolled
    }


    /**
     * 填充子控件
     */
    override fun layoutChildView(view:View,recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val top: Int
        val bottom: Int
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        //width+分隔线+左右margin,控制右排版位置
        val right = orientationHelper.getDecoratedMeasurementInOther(view)+paddingRight
        if (layoutState.itemDirection == DIRECTION_START) {
            bottom = layoutState.layoutOffset
            top = layoutState.layoutOffset - consumed
        } else {
            top = layoutState.layoutOffset
            bottom = layoutState.layoutOffset + consumed
        }
        //scrollX内己包含paddingLeft数值
        layoutDecorated(view, scrollX, top, right+scrollX, bottom)
        debugLog("layoutChildView paddingLeft:$paddingLeft scrollX:$scrollX top:$top right:${right+scrollX} bottom:$bottom consumed:${view.measuredWidth}")
        //返回控件高度/宽
        return consumed
    }

    override fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        val view=super.nextView(recycler,state) as? TableColumnLayout ?: throw RuntimeException("必须使用TableColumnLayout作用根布局!")
        //初始化item属性
        view.setPadding(itemPadding.left, itemPadding.top,itemPadding.right,itemPadding.bottom)
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(itemBackgroundDrawable?.constantState?.newDrawable())
        } else {
            view.background=itemBackgroundDrawable?.constantState?.newDrawable()
        }
        //点击事件
        view.setOnClickListener { onItemClickListener?.onItemClick(it,getPosition(it)) }
        //长按事件
        view.setOnLongClickListener{ onItemLongClickListener?.onItemLongClick(it,getPosition(it))?:false }
        return view
    }

    class ItemPadding{
        var padding:Int=0
        var left:Int=0
            get()=if(0==field) padding else field
        var top:Int=0
            get()=if(0==field) padding else field
        var right:Int=0
            get()=if(0==field) padding else field
        var bottom:Int=0
            get()=if(0==field) padding else field

    }

    fun setOnItemClickListener(listener: OnTableItemClickListener) {
        this.onItemClickListener=listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener=listener
    }
}
