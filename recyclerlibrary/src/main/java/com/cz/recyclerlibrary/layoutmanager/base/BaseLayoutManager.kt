package com.cz.recyclerlibrary.layoutmanager.base

import android.content.Context
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import android.support.v7.widget.RecyclerView.NO_POSITION
import com.cz.recyclerlibrary.debugLog


/**
 * Created by cz on 2017/1/20.
 * 一个支持横向纵向排版的layoutManager,为 LayoutManager 最精简的核心代码.负责控件排版方向,控件复用等代码
 * @update by cz on 2017/9/14
 * 1:增加无限循环机制
 * 2:增加起始居中机制
 */
abstract class BaseLayoutManager : RecyclerView.LayoutManager {
    companion object {
        val HORIZONTAL = OrientationHelper.HORIZONTAL
        val VERTICAL = OrientationHelper.VERTICAL
    }
    protected val layoutState: BaseLayoutManager.LayoutState
    protected lateinit var orientationHelper: OrientationHelper
    /**
     * 当前排版方向
     * @see {@link .HORIZONTAL} or {@link .VERTICAL}
     */
    private var orientation: Int = VERTICAL

    @JvmOverloads constructor(orientation: Int = VERTICAL) {
        layoutState = LayoutState()
        setOrientation(orientation)
    }

    /**
     * 从RecyclerView 处初始化代码,顺应recyclerView 初始化逻辑
     * 具体流程见:[android.support.v7.widget.RecyclerView.createLayoutManager]
     * @param context
     * *
     * @param attrs
     * *
     * @param defStyleAttr
     * *
     * @param defStyleRes
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        layoutState = BaseLayoutManager.LayoutState()
        val properties = RecyclerView.LayoutManager.getProperties(context, attrs, defStyleAttr, defStyleRes)
        setOrientation(properties.orientation)
    }

    /**
     * 设置方向
     * @param orientation
     */
    open fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw IllegalArgumentException("invalid orientation:" + orientation)
        }
        this.orientation = orientation
        if (OrientationHelper.HORIZONTAL == orientation) {
            orientationHelper = OrientationHelper.createHorizontalHelper(this)
        } else if (OrientationHelper.VERTICAL == orientation) {
            orientationHelper = OrientationHelper.createVerticalHelper(this)
        }
    }

    fun getOrientation()=orientation

    override fun generateDefaultLayoutParams() =RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    /**
     * 初始化布局状态
     * @param offset 排版起始位置,用于部分 LayoutManager 起始居中
     */
    protected fun updateLayoutStateToFillEnd(itemPosition:Int, offset: Int) {
        layoutState.offset = offset
        layoutState.currentPosition = itemPosition
        layoutState.available = orientationHelper.totalSpace
        layoutState.itemDirection = LayoutState.ITEM_DIRECTION_TAIL
        layoutState.layoutDirection = LayoutState.LAYOUT_END
    }

    /**
     * 此方法会调用俩次,所有在最初填充/更新信息时,清空所有 view,且始终重置 layoutState 状态.然后再填充
     * @param recycler
     * *
     * @param state
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (0 == itemCount||state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
        } else if(state.didStructureChange()){
            detachAndScrapAttachedViews(recycler)
            //初始化/重置 layoutState 状态
            updateLayoutStateToFillEnd(0,0)
            //首次填充控件
            fill(recycler, state)
        }
    }

    /**
     * 填充己有空间
     * @param recycler
     * *
     * @param state
     */
    protected open fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val start = layoutState.available
        if (0 > layoutState.available) {
            layoutState.scrollOffset += layoutState.available
        }
        debugLog("scrollingOffset:" + layoutState.scrollOffset + " available:" + layoutState.available)
        recycleByLayoutState(recycler, layoutState)
        var space = layoutState.available
        //获取当前可填充空间大小
        while (0 < space && layoutState.hasMore(state)) {
            val consumed = layoutChildView(recycler,state)
            space -= consumed
            layoutState.available -= consumed
            layoutState.offset += consumed * layoutState.layoutDirection
        }
        return start - space
    }

    /**
     * 加入子控件
     */
    protected open fun layoutChildView(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val view = next(layoutState, recycler,state) ?: return 0
        //根据 layoutDirection 添加控件前或者后
        if (LayoutState.LAYOUT_END == layoutState.layoutDirection) {
            addView(view)
        } else if (LayoutState.LAYOUT_START == layoutState.layoutDirection) {
            addView(view, 0)
        }
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        measureChildWithMargins(view, 0, 0)
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        if (HORIZONTAL == orientation) {
            bottom = orientationHelper.getDecoratedMeasurementInOther(view)
            if (LayoutState.LAYOUT_START == layoutState.layoutDirection) {
                left = layoutState.offset - consumed
                right = layoutState.offset
            } else if (LayoutState.LAYOUT_END == layoutState.layoutDirection) {
                left = layoutState.offset
                right = layoutState.offset + consumed
            }
        } else if (VERTICAL == orientation) {
            left = paddingLeft
            right = orientationHelper.getDecoratedMeasurementInOther(view)
            if (LayoutState.LAYOUT_START == layoutState.layoutDirection) {
                top = layoutState.offset - consumed
                bottom = layoutState.offset
            } else if (LayoutState.LAYOUT_END == layoutState.layoutDirection) {
                top = layoutState.offset
                bottom = layoutState.offset + consumed
            }
        }
        layoutDecorated(view, left, top, right, bottom)
        debugLog("left:$left top:$top right:$right bottom:$bottom")
        return consumed
    }


    override fun canScrollHorizontally(): Boolean {
        return HORIZONTAL == orientation
    }

    override fun canScrollVertically(): Boolean {
        return VERTICAL == orientation
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollBy(dy, recycler, state)
    }

    protected open fun scrollBy(distance: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollToStart(distance, recycler, state)
    }

    protected fun scrollToStart(distance: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val layoutDirection = if (0 > distance) BaseLayoutManager.LayoutState.LAYOUT_START else BaseLayoutManager.LayoutState.LAYOUT_END
        val absDistance = Math.abs(distance)
        updateLayoutState(layoutDirection, absDistance)
        val consumed = layoutState.scrollOffset + fill(recycler, state)
        val scrolled = if (absDistance > consumed) consumed * layoutDirection else distance
        orientationHelper.offsetChildren(-scrolled)
        return scrolled
    }

    /**
     * 更新 layoutState 状态,上滑预设置 currentPosition-1,下滑+1 配对预设的 LayoutDirection值
     * @param layoutDirection
     * *
     * @param absDistance
     */
    protected fun updateLayoutState(layoutDirection: Int, absDistance: Int) {
        layoutState.layoutDirection = layoutDirection
        var scrollOffset = 0
        if (layoutDirection == BaseLayoutManager.LayoutState.LAYOUT_END) {
            val childView = getChildAt(childCount - 1)
            layoutState.itemDirection = LayoutState.ITEM_DIRECTION_TAIL
            layoutState.currentPosition = getPosition(childView) + layoutState.itemDirection
            //下滑取最后一个控件的底部位置,一般为负数,大于0时,才表示可以添加新的控件了
            layoutState.offset = orientationHelper.getDecoratedEnd(childView)
            scrollOffset = orientationHelper.getDecoratedEnd(childView) - orientationHelper.endAfterPadding
        } else if (layoutDirection == LayoutState.LAYOUT_START) {
            val childView = getChildAt(0)
            layoutState.itemDirection = LayoutState.ITEM_DIRECTION_HEAD
            layoutState.currentPosition = getPosition(childView) + layoutState.itemDirection
            layoutState.offset = orientationHelper.getDecoratedStart(childView)
            scrollOffset = -layoutState.offset
        }
        layoutState.available = absDistance - scrollOffset
        layoutState.scrollOffset = scrollOffset
        debugLog("offset:" + layoutState.offset + " available:" + layoutState.available + " scrollingOffset:" + scrollOffset + " position:" + layoutState.currentPosition + " layoutDirection:" + layoutDirection)
    }

    private fun recycleByLayoutState(recycler: RecyclerView.Recycler, layoutState: BaseLayoutManager.LayoutState) {
        if (layoutState.layoutDirection == BaseLayoutManager.LayoutState.LAYOUT_START) {
            recycleViewsFromEnd(recycler, layoutState.scrollOffset)
        } else {
            recycleViewsFromStart(recycler, layoutState.scrollOffset)
        }
    }

    private fun recycleViewsFromStart(recycler: RecyclerView.Recycler, scrollOffset: Int) {
        val childCount = childCount
        if (0 < childCount) {
            for (i in 0..childCount - 1) {
                val child = getChildAt(i)
                if (orientationHelper.getDecoratedEnd(child) > scrollOffset) {
                    recycleChildren(recycler, 0, i)
                    break
                }
            }
        }
    }

    private fun recycleViewsFromEnd(recycler: RecyclerView.Recycler, scrollOffset: Int) {
        if (0 < scrollOffset) {
            val childCount = childCount
            val limit = orientationHelper.end - scrollOffset
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)
                if (orientationHelper.getDecoratedStart(child) < limit) {
                    recycleChildren(recycler, childCount - 1, i)
                    break
                }
            }
        }
    }

    /**
     * 执行控件回收,这里注意,同一类 view,默认 RecyclerPool会默认缓存5个,超出5个后才开始真正回收,这时候走 Adapter#onViewRecycled
     * @param recycler
     * *
     * @param startIndex
     * *
     * @param endIndex
     */
    private fun recycleChildren(recycler: RecyclerView.Recycler, startIndex: Int, endIndex: Int) {
        if (startIndex < endIndex) {
            for (i in endIndex - 1 downTo startIndex) {
                removeAndRecycleViewAt(i, recycler)
            }
        } else if (startIndex > endIndex) {
            for (i in startIndex downTo endIndex + 1) {
                removeAndRecycleViewAt(i, recycler)
            }
        }
    }

    fun findFirstVisibleItemPosition(): Int {
        val child = findOneVisibleChild(0, childCount, false, true)
        return if (child == null) NO_POSITION else getPosition(child)
    }

    fun findLastVisibleItemPosition(): Int {
        val child = findOneVisibleChild(childCount - 1, -1, false, true)
        return if (child == null) NO_POSITION else getPosition(child)
    }


    internal fun findOneVisibleChild(fromIndex: Int, toIndex: Int, completelyVisible: Boolean,
                                     acceptPartiallyVisible: Boolean): View? {
        val start = orientationHelper.startAfterPadding
        val end = orientationHelper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var partiallyVisible: View? = null
        var i = fromIndex
        while (i != toIndex) {
            val child = getChildAt(i)
            val childStart = orientationHelper.getDecoratedStart(child)
            val childEnd = orientationHelper.getDecoratedEnd(child)
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child
                    }
                } else {
                    return child
                }
            }
            i += next
        }
        return partiallyVisible
    }

    protected open fun next(layoutState: LayoutState, recycler: RecyclerView.Recycler,state: RecyclerView.State): View? {
        val nextView = recycler.getViewForPosition(layoutState.currentPosition)
        layoutState.currentPosition += layoutState.itemDirection
        return nextView
    }

    /**
     * 布局状态控制对象
     */
    protected class LayoutState {

        var offset: Int = 0

        var available: Int = 0

        var currentPosition: Int = 0

        var layoutDirection: Int = 0

        var itemDirection: Int = 0

        var scrollOffset: Int = 0

        fun hasMore(state: RecyclerView.State): Boolean {
            return currentPosition >= 0 && currentPosition < state.itemCount
        }

        companion object {
            val LAYOUT_START = -1
            val LAYOUT_END = 1
            val ITEM_DIRECTION_HEAD = -1
            val ITEM_DIRECTION_TAIL = 1
        }
    }
}
