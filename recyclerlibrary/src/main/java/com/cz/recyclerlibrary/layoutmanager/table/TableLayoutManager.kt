package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.SparseIntArray
import android.view.View

import com.cz.recyclerlibrary.layoutmanager.base.BaseLayoutManager

/**
 * Created by cz on 2017/1/20.
 * 一个支持横向内容超出的列表,应用场景为展示数据库界面,以及事件列表
 * 因特殊的布局控制,所有外层必须 使用[TableColumnLayout]
 * @see {@link TableColumnLayout} 为一个支持横向向前排序的线性容器,而 linearLayout 最大尺寸为屏幕宽,所以无法支持此超出屏table设计
 * 此做法存在一些性能问题,如屏幕外一口气会加载过多控件.但设计上更符合如数据库这类设计.
 */
class TableLayoutManager : BaseLayoutManager {
    private val columnItemSize: SparseIntArray
    private var totalWidth: Int = 0

    constructor() : super(BaseLayoutManager.VERTICAL) {
        columnItemSize = SparseIntArray()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        super.setOrientation(BaseLayoutManager.VERTICAL)
        columnItemSize = SparseIntArray()
    }

    /**
     * @param orientation
     */
    override fun setOrientation(orientation: Int) {
        super.setOrientation(BaseLayoutManager.VERTICAL)
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (0 == itemCount||state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
        } else if (state.didStructureChange()) {
            detachAndScrapAttachedViews(recycler)
            //初始化/重置 layoutState 状态
            updateLayoutStateToFillEnd(layoutState.currentPosition,layoutState.offset)
            ensureColumnItem(recycler)
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
        val layoutDirection = if (0 > dx) BaseLayoutManager.LayoutState.LAYOUT_START else BaseLayoutManager.LayoutState.LAYOUT_END
        var scrolled = dx
        if (BaseLayoutManager.LayoutState.LAYOUT_START == layoutDirection) {
            //to left
            if (0 < decoratedLeft) {
                scrolled = 0
            } else if (Math.abs(dx) + decoratedLeft > 0) {
                scrolled = decoratedLeft
            }
        } else if (BaseLayoutManager.LayoutState.LAYOUT_END == layoutDirection) {
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

    /**
     * 加入子控件,复写子类,为避免横向滑动时,排版问题.
     * 排版得以第一个子孩子的相对左/右位置排版,不能以 paddingLeft 来排,因为任何滑到任何位置,paddingLeft都是0,所以会横向排版错乱,如果没有第一个孩子,默认就是0
     */
    override fun layoutChildView(recycler: RecyclerView.Recycler,state: RecyclerView.State): Int {
        val view = next(layoutState, recycler,state) ?: return 0
//处理 offset
        var decoratedLeft = 0
        if (0 < childCount) {
            val childView = getChildAt(0)
            decoratedLeft = getDecoratedLeft(childView)
        }
        //根据 layoutDirection 添加控件前或者后
        if (BaseLayoutManager.LayoutState.LAYOUT_END == layoutState.layoutDirection) {
            addView(view)
        } else if (BaseLayoutManager.LayoutState.LAYOUT_START == layoutState.layoutDirection) {
            addView(view, 0)
        }
        measureChildWithMargins(view, 0, 0)
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        val left = decoratedLeft
        var top = 0
        val right = decoratedLeft + getDecoratedMeasuredWidth(view)
        var bottom = 0
        if (BaseLayoutManager.LayoutState.LAYOUT_START == layoutState.layoutDirection) {
            top = layoutState.offset - consumed
            bottom = layoutState.offset
        } else if (BaseLayoutManager.LayoutState.LAYOUT_END == layoutState.layoutDirection) {
            top = layoutState.offset
            bottom = layoutState.offset + consumed
        }
        layoutDecorated(view, left, top, right, bottom)
        return consumed
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun next(layoutState: BaseLayoutManager.LayoutState, recycler: RecyclerView.Recycler,state: RecyclerView.State): View? {
        val columnLayout = super.next(layoutState, recycler,state) as TableColumnLayout?
        if (0 < columnItemSize.size()) {
            columnLayout!!.setColumnSize(columnItemSize)
        }
        return columnLayout
    }

    companion object {
        private val TAG = "TableLayoutManager"
    }
}
