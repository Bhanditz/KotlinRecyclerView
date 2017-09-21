package com.cz.recyclerlibrary.layoutmanager.wheel

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.cz.recyclerlibrary.R

import com.cz.recyclerlibrary.layoutmanager.base.CenterBaseLayoutManager
import com.cz.recyclerlibrary.layoutmanager.base.CenterScrollListener


/**
 * Created by Administrator on 2017/1/15.

 */
class WheelLayoutManager : CenterBaseLayoutManager {
    private var wheelEnable: Boolean = false
    private var wheelCount: Int = 0

    /**
     * 默认构造的排版方向为向下
     */
    constructor() : super(VERTICAL) {
        setWheelCount(3)
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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WheelView, defStyleAttr, defStyleRes)
        setWheelCount(a.getInteger(R.styleable.WheelView_wv_wheelCount, 3))
        setWheelEnable(a.getBoolean(R.styleable.WheelView_wv_wheelEnable, true))
        setMinScrollOffset(a.getFloat(R.styleable.WheelView_wv_minScrollOffset, 0f))
    }


    fun setWheelCount(itemCount: Int) {
        if (0 == itemCount % 2) {
            throw IllegalArgumentException("error wheel count!")
        } else {
            this.wheelCount = itemCount
            requestLayout()
        }
    }

    /**
     * 是否启用Wheel滑动
     * @param enable
     */
    fun setWheelEnable(enable: Boolean) {
        this.wheelEnable = enable
    }


    /**
     * 当装载入 RecyclerView时,设置固定布局(即布局大小,由 LayoutManager 的 onMeasure 控件),以及添加滚动完居中监听
     * @param view
     */
    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        //设置不自动RecyclerView 不自由排版,且固定尺寸
        isAutoMeasureEnabled = false
        view.setHasFixedSize(true)
        view.addOnScrollListener(CenterScrollListener(this))
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        val measuredWidth = View.MeasureSpec.getSize(widthSpec)
        var measuredHeight = View.MeasureSpec.getSize(heightSpec)
        if (0 < itemCount && 0 < state.itemCount) {
            val firstView = recycler.getViewForPosition(0)
            if (null != firstView) {
                measureChildWithMargins(firstView, 0, 0)
                measuredHeight = firstView.measuredHeight * wheelCount
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val itemCount = itemCount
        if (0 == itemCount||state.isPreLayout) {
            detachAndScrapAttachedViews(recycler)
        } else {
            detachAndScrapAttachedViews(recycler)
            val view = recycler.getViewForPosition(0)
            measureChildWithMargins(view, 0, 0)
            updateLayoutStateToFillEnd(0,(height - getDecoratedMeasuredHeight(view)) / 2)
            fill(recycler, state)
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    /**
     * 是否启用纵向滑动
     * @return
     */
    override fun canScrollVertically(): Boolean {
        return wheelEnable
    }
}
