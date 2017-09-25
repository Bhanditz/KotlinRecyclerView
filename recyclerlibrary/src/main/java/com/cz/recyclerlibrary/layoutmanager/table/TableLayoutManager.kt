package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.SparseIntArray
import android.view.View
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager


/**
 * Created by cz on 2017/1/20.
 * 一个支持横向内容超出的列表,应用场景为展示数据库界面,以及事件列表
 * 因特殊的布局控制,所有外层必须 使用[TableColumnLayout]
 * @see {@link TableColumnLayout} 为一个支持横向向前排序的线性容器,而 linearLayout 最大尺寸为屏幕宽,所以无法支持此超出屏table设计
 * 此做法存在一些性能问题,如屏幕外一口气会加载过多控件.但设计上更符合如数据库这类设计.
 */
class TableLayoutManager : BaseLinearLayoutManager {
    private val columnItemSize: SparseIntArray = SparseIntArray()
    private var totalWidth: Int = 0

    constructor() : super(BaseLinearLayoutManager.VERTICAL) {
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (0 == itemCount||state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
        } else if (state.didStructureChange()) {
            detachAndScrapAttachedViews(recycler)
            //初始化/重置 layoutState 状态
//            updateLayoutStateToFillEnd(recycler,state)
//            ensureColumnItem(recycler)
            //首次填充控件
            fill(recycler, state)
        }
    }

    /**
     * 预计算第一个控件个数宽,为以后每个列控件宽
     * @param recycler
     */
    private fun ensureColumnItem(recycler: RecyclerView.Recycler) {
        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view, 0, 0)
        if (view !is TableColumnLayout) {
            throw IllegalArgumentException("the container layout must used TableColumnLayout!")
        } else {
            val tableColumnLayout = view
            //计算每一个孩子宽,设定为后续每一列宽
            val childCount = tableColumnLayout.childCount
            if (0 < childCount) {
                columnItemSize.clear()
                totalWidth = getDecoratedMeasuredWidth(tableColumnLayout)
                for (i in 0..childCount - 1) {
                    val childView = tableColumnLayout.getChildAt(i)
                    measureChild(childView, 0, 0)
                    val childMeasuredWidth = childView.measuredWidth
                    columnItemSize.put(i, childMeasuredWidth)
                }
            }
        }
    }

    override fun measureChild(child: View, widthUsed: Int, heightUsed: Int) {
        val lp = child.layoutParams
        val widthSpec = RecyclerView.LayoutManager.getChildMeasureSpec(width, widthMode, paddingLeft + paddingRight + widthUsed, lp.width, canScrollHorizontally())
        val heightSpec = RecyclerView.LayoutManager.getChildMeasureSpec(height, heightMode, paddingTop + paddingBottom + heightUsed, lp.height, canScrollVertically())
        child.measure(widthSpec, heightSpec)
    }

    override fun canScrollHorizontally(): Boolean {
        return totalWidth > width
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (0 == itemCount) {
            return 0
        }
        val childView = getChildAt(0)
        val leftDecorationWidth = getRightDecorationWidth(childView)
        val decoratedLeft = getDecoratedLeft(childView)
        val decoratedRight = getDecoratedRight(childView) - width
        val layoutDirection = if (0 > dx) BaseLinearLayoutManager.DIRECTION_START else BaseLinearLayoutManager.DIRECTION_END
        var scrolled = dx
        if (BaseLinearLayoutManager.DIRECTION_START == layoutDirection) {
            //to left
            if (0 < decoratedLeft) {
                scrolled = 0
            } else if (Math.abs(dx) + decoratedLeft > 0) {
                scrolled = decoratedLeft
            }
        } else if (BaseLinearLayoutManager.DIRECTION_END == layoutDirection) {
            //to right
            if (0 > decoratedRight) {
                scrolled = 0
            } else if (decoratedRight - dx < 0) {
                scrolled = decoratedRight
            }
        }
        offsetChildrenHorizontal(-scrolled)
        Log.e(TAG, "decoratedLeft:$decoratedLeft decoratedRight:$decoratedRight dx:$dx scrolled:$scrolled leftDecorationWidth:$leftDecorationWidth")
        return scrolled
    }

    override fun layoutChildView(view: View, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //处理 offset
        var decoratedLeft = 0
        if (0 < childCount) {
            val childView = getChildAt(0)
            decoratedLeft = getDecoratedLeft(childView)
        }
        //根据 layoutDirection 添加控件前或者后
        if (BaseLinearLayoutManager.DIRECTION_END == layoutState.itemDirection) {
            addView(view)
        } else if (BaseLinearLayoutManager.DIRECTION_START == layoutState.itemDirection) {
            addView(view, 0)
        }
        measureChildWithMargins(view, 0, 0)
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        val left = decoratedLeft
        var top = 0
        val right = decoratedLeft + getDecoratedMeasuredWidth(view)
        var bottom = 0
        if (BaseLinearLayoutManager.DIRECTION_START == layoutState.itemDirection) {
            top = layoutState.layoutOffset - consumed
            bottom = layoutState.layoutOffset
        } else if (BaseLinearLayoutManager.DIRECTION_END == layoutState.itemDirection) {
            top = layoutState.layoutOffset
            bottom = layoutState.layoutOffset + consumed
        }
        layoutDecorated(view, left, top, right, bottom)
        return consumed
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        val columnLayout = super.nextView(recycler,state) as TableColumnLayout
        if (0 < columnItemSize.size()) {
            columnLayout.setColumnSize(columnItemSize)
        }
        return columnLayout
    }

    companion object {
        private val TAG = "TableLayoutManager"
    }
}
