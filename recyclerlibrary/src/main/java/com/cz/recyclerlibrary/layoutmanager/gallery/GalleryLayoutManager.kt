package com.cz.recyclerlibrary.layoutmanager.gallery

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.layoutmanager.base.CenterBaseLayoutManager
import com.cz.recyclerlibrary.layoutmanager.base.CenterScrollListener

/**
 * Created by cz on 1/18/17.
 * 一个 gallery 的布局管理器对象支持横向纵向操作
 * 继承 BaseLayoutManager 获得横向纵向排版以及滑动的支持.
 * 1:排版支持:start/center
 */
class GalleryLayoutManager : CenterBaseLayoutManager {
    private var gravity: Int = 0

    @JvmOverloads constructor(orientation: Int = HORIZONTAL, gravity: Int = START) : super(orientation) {
        this.gravity = gravity
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.GalleryView, defStyleAttr, defStyleRes)
        setGravity(a.getInt(R.styleable.GalleryView_gv_gravity, CENTER))
        setMinScrollOffset(a.getFloat(R.styleable.GalleryView_gv_minScrollOffset, MIN_SCROLL_OFFSET))
        a.recycle()
    }

    /**
     * 设置起始排版方向
     * @param gravity
     */
    fun setGravity(gravity: Int) {
        this.gravity = gravity
        requestLayout()
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        //设置滑动完成居中
        view!!.addOnScrollListener(CenterScrollListener(this))
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val itemCount = itemCount
        if (0 == itemCount) {
            detachAndScrapAttachedViews(recycler)
        } else if (!state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
            val view = recycler.getViewForPosition(0)
            measureChildWithMargins(view, 0, 0)
            //控件起始排版方向
            if (START == gravity) {
                updateLayoutStateToFillEnd(0,0)
            } else if (CENTER == gravity) {
                updateLayoutStateToFillEnd(layoutState.currentPosition,(orientationHelper.end - orientationHelper.getDecoratedMeasurement(view)) / 2)
            }
            fill(recycler, state)
        }
    }

    /**
     * 完全复写,根据 gravity 设定是否靠上,或者居中
     * @param distance
     * *
     * @param recycler
     * *
     * @param state
     * *
     * @return
     */
    override fun scrollBy(distance: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || distance == 0) {
            return 0
        }
        var scrolled = 0
        if (START == gravity) {
            //滚动靠顶部
            scrolled = scrollToStart(distance, recycler, state)
        } else if (CENTER == gravity) {
            scrolled = scrollToCenter(distance, recycler, state)
        }
        return scrolled
    }

    companion object {
        private val TAG = "GalleryLayoutManager"
        private val MIN_SCROLL_OFFSET = 0.6f
        val START = 0
        val CENTER = 1
    }

}