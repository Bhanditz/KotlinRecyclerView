package com.cz.recyclerlibrary.layoutmanager.base

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import android.support.v7.widget.RecyclerView.NO_POSITION
import com.cz.recyclerlibrary.debugLog


/**
 * Created by cz on 2017/1/20.
 * 在 BaseLayoutManager 上进一步封装了,控件点击滑动让控件居中,缓慢滚动居中
 */
open class CenterBaseLayoutManager : BaseLayoutManager {
    private var centerSmoothScroller: CenterSmoothScroller? = null
    private var minScrollOffset: Float = 0.toFloat()

    constructor(orientation: Int) : super(orientation)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setMinScrollOffset(minScrollOffset: Float) {
        this.minScrollOffset = minScrollOffset
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        //因快速滑动过程结束后,控件的属性变化并不统一,这里强制统一
        if (0 < itemCount && RecyclerView.SCROLL_STATE_IDLE == state) {
            (0..childCount - 1).map { getChildAt(it) }.forEach(this::viewScrollOffset)
        }
    }


    override fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val fill = super.fill(recycler, state)
        //取中心点,作滑动动作设定
        (0..childCount - 1).map { getChildAt(it) }.forEach(this::viewScrollOffset)
        return fill
    }

    /**
     * 当控件滑动位置发生偏移,使用此方法需要用户Adapter实现[ViewScrollOffsetCallback]
     * 在此方法中,可完成具体控件的动画控制
     * @param childView
     */
    protected fun viewScrollOffset(childView: View) {
        val center = orientationHelper.end / 2
        val childCenter = orientationHelper.getDecoratedStart(childView) + orientationHelper.getDecoratedMeasurement(childView) / 2
        var offset = 1f - Math.abs(childCenter - center) * 1f / center
        if (offset < minScrollOffset) {
            offset = minScrollOffset
        }
        //动态设定adapter字体样式
        val parent = childView.parent
        if (null != parent) {
            val recyclerView = childView.parent as RecyclerView
            val adapter = recyclerView.adapter
            if (null != adapter && adapter is ViewScrollOffsetCallback) {
                adapter.onViewScrollOffset(childView, getPosition(childView), findCurrentItemPosition(), offset)
            }
        }
    }


    override fun scrollBy(distance: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollToCenter(distance, recycler, state)
    }

    /**
     * 滚动居中
     * @param distance
     * *
     * @param recycler
     * *
     * @param state
     * *
     * @return
     */
    protected fun scrollToCenter(distance: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || distance == 0) {
            return 0
        }
        val layoutDirection = if (distance > 0) BaseLayoutManager.LayoutState.LAYOUT_END else BaseLayoutManager.LayoutState.LAYOUT_START
        val absDy = Math.abs(distance)
        updateLayoutState(layoutDirection, absDy)
        val consumed = layoutState.scrollOffset + fill(recycler, state)
        var scrolled = distance
        //缓慢滑动时,有一定机率造成 consumed==center而导致滑动暂停
        if (BaseLayoutManager.LayoutState.LAYOUT_START == layoutDirection && 0 == findFirstVisibleItemPosition()) {
            //上滑至顶
            val childView = getChildAt(0)
            val measurement = orientationHelper.getDecoratedMeasurement(childView)
            val center = (orientationHelper.end - measurement) / 2
            val visibleItemPosition = findFirstVisibleItemPosition()
            //这里存在不在顶部但是 consumed==centerY 的情况,导致无法滑动的情况
            if (Math.abs(consumed) == center && 0 == visibleItemPosition) {
                scrolled = 0
            } else if (consumed < -center) {
                //缓慢滑动,一次滑动距离小于当前距离
                scrolled = Math.abs(consumed) - center
            } else if (distance < -center) {
                //快速滑动,一次滑出的距离远超出当前顶部控件位置
                scrolled = consumed * layoutDirection
            }
            debugLog("scrolled distance<-center:$scrolled consumed:$consumed center:$center distance:$distance")
        } else if (BaseLayoutManager.LayoutState.LAYOUT_END == layoutDirection && layoutState.currentPosition == itemCount) {
            //下滑至底
            val childView = getChildAt(childCount - 1)
            val measurement = orientationHelper.getDecoratedMeasurement(childView)
            val center = (orientationHelper.end - measurement) / 2
            val visibleItemPosition = findLastVisibleItemPosition()
            //这里存在不在底部但是 consumed==centerY 的情况,导致无法滑动的情况
            if (Math.abs(consumed) == center && visibleItemPosition == itemCount - 1) {
                scrolled = 0
            } else if (consumed < -center) {
                //底部越界检测consumed为负数若越界则会超出-centerY大小
                scrolled = center + consumed
            }
            debugLog("scrolled End:$scrolled consumed:$consumed center:$center distance:$distance")
        }
        orientationHelper.offsetChildren(-scrolled)
        return scrolled
    }


    /**
     * 缓慢滚动,需要考虑几次因素
     * 1:复写 LinearSmoothScroller,更改滚动时间
     * 2:将WheelSmoothScroller定义为成员,而不是每次返回,以获取正常可用的 isSmoothScrolling()方法返回值,具体可查看其源码
     * 3:避免 WheelCenterScrollListener 的滑动停止居中与smoothScroll的滑动事件冲突
     * @param recyclerView
     * *
     * @param state
     * *
     * @param position
     */
    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        //检测 position 是否在当前屏内
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        if (position in firstVisibleItemPosition..lastVisibleItemPosition) {
            //直接移动,当控件为最后1屏时.控件高度位置并不准确,所以采用直接的index 计算
            val childView = findViewByPosition(position)
            val offset = orientationHelper.getDecoratedStart(childView) + orientationHelper.getDecoratedMeasurement(childView) / 2 - orientationHelper.end / 2
            if (canScrollHorizontally()) {
                recyclerView!!.smoothScrollBy(offset, 0)
            } else if (canScrollVertically()) {
                recyclerView!!.smoothScrollBy(0, offset)
            }
        } else {
            //复写 smoothLinearScroller 修改滚动时间,这里不能像系统 LinearLayoutManager 一样始终返回新的对象.会导致 LayoutManager#isSmoothScrolling失效
            if (null == centerSmoothScroller) {
                centerSmoothScroller = object : CenterSmoothScroller(recyclerView!!.context) {
                    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                        return smoothScrollComputeScrollVectorForPosition(targetPosition)
                    }
                }
            }
            centerSmoothScroller!!.targetPosition = position
            startSmoothScroll(centerSmoothScroller)
        }
    }

    fun smoothScrollComputeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0))
        val direction = if (targetPosition < firstChildPos) -1 else 1
        return PointF(0f, direction.toFloat())
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

    fun findCurrentItemPosition(): Int {
        val child = findOneVisibleChild(0, childCount)
        return if (child == null) NO_POSITION else getPosition(child)
    }

    internal fun findOneVisibleChild(fromIndex: Int, toIndex: Int): View? {
        val next = if (toIndex > fromIndex) 1 else -1
        val partiallyVisible: View? = null
        val centerY = orientationHelper.end / 2
        var i = fromIndex
        while (i != toIndex) {
            val child = getChildAt(i)
            val childStart = orientationHelper.getDecoratedStart(child)
            val childEnd = orientationHelper.getDecoratedEnd(child)
            if (centerY in childStart..childEnd) {
                return child
            }
            i += next
        }
        return partiallyVisible
    }

    override fun next(layoutState: BaseLayoutManager.LayoutState, recycler: RecyclerView.Recycler,state: RecyclerView.State): View? {
        val view = super.next(layoutState, recycler,state)
        view?.setOnClickListener { v ->
            val recyclerView = v.parent as RecyclerView
            val position = getPosition(v)
            if (RecyclerView.NO_POSITION != position) {
                recyclerView.smoothScrollToPosition(position)
            }
        }
        return view
    }
}
