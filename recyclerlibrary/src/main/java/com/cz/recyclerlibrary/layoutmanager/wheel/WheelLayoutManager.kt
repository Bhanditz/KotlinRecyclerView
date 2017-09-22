package com.cz.recyclerlibrary.layoutmanager.wheel

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.debugLog

import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager


/**
 * Created by cz on 2017/9/22.
 */
class WheelLayoutManager(orientation: Int) : CenterLinearLayoutManager(orientation) {
    private var wheelCount: Int = 3
    init {
        //设置不自动RecyclerView 不自由排版,且固定尺寸
        isAutoMeasureEnabled = false
    }

    fun setWheelCount(itemCount: Int) {
        this.wheelCount = itemCount
        requestLayout()
    }

    /**
     * 当装载入 RecyclerView时,设置固定布局(即布局大小,由 LayoutManager 的 onMeasure 控件),以及添加滚动完居中监听
     * @param view
     */
    override fun onAttachedToWindow(view: RecyclerView) {
        view.setHasFixedSize(true)
        super.onAttachedToWindow(view)
    }

    /**
     * 重新测量
     */
    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        debugLog("onMeasure")
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

}
