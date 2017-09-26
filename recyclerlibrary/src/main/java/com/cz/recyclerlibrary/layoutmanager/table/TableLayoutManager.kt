package com.cz.recyclerlibrary.layoutmanager.table

import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
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
    private lateinit var columnArray:IntArray
    private var totalWidth: Int = 0
    private var scrollX=0
    constructor() : super(BaseLinearLayoutManager.VERTICAL)

    /**
     * 此处复写,完成第一次排版时,计算每一列宽度问题
     */
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
            if(view is TableColumnLayout){
                if(layoutState.layoutChildren){
                    //初始化排版
                    totalWidth = getDecoratedMeasuredWidth(view)
                    var array= (0..view.childCount - 1).map { view.getChildAt(it).measuredWidth }
                    columnArray=array.toIntArray()
                    layoutState.layoutChildren=false
                }
                view.setColumnSize(columnArray)
            }
            //添加控件
            addAdapterView(view)
            val consumed= layoutChildView(view,recycler,state)
            layoutState.layoutOffset +=consumed*layoutState.itemDirection
            layoutState.available-=consumed
            remainingSpace-=consumed
        }
        //返回排版后,所占用空间
        return start-layoutState.available
    }

    override fun canScrollHorizontally(): Boolean {
        return totalWidth>width
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (0 == itemCount) {
            return 0
        }
        val childView = getChildAt(0)
        val decoratedLeft = getDecoratedLeft(childView)
        val decoratedRight = getDecoratedRight(childView) - width
        val layoutDirection = if (0 > dx) DIRECTION_START else DIRECTION_END
        var scrolled = dx
        if (DIRECTION_START == layoutDirection) {
            //to left
            if (0 < decoratedLeft) {
                scrolled = 0
            } else if (Math.abs(dx) + decoratedLeft > 0) {
                scrolled = decoratedLeft
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
        return scrolled
    }


    /**
     * 填充子控件
     */
    override fun layoutChildView(view:View,recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var left: Int=paddingLeft
        val top: Int
        val right: Int
        val bottom: Int
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        //width+分隔线+左右margin,控制右排版位置
        right = left + orientationHelper.getDecoratedMeasurementInOther(view)
        if (layoutState.itemDirection == DIRECTION_START) {
            bottom = layoutState.layoutOffset
            top = layoutState.layoutOffset - consumed
        } else {
            top = layoutState.layoutOffset
            bottom = layoutState.layoutOffset + consumed
        }
        layoutDecorated(view, scrollX, top, right+scrollX, bottom)
        //返回控件高度/宽
        return consumed
    }

    override fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        return super.nextView(recycler,state) as? TableColumnLayout ?: throw RuntimeException("必须使用TableColumnLayout作用根布局!")
    }
}
