package com.cz.recyclerlibrary.layoutmanager.wheel

import android.support.v7.widget.RecyclerView
import android.view.View

import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager


/**
 * Created by cz on 2017/9/22.
 */
class WheelLayoutManager(orientation: Int) : CenterLinearLayoutManager(orientation) {
    private var wheelCount: Int = 5

    fun setWheelCount(itemCount: Int) {
        this.wheelCount = itemCount
        requestLayout()
    }

    /**
     * 当装载入 RecyclerView时,设置固定布局(即布局大小,由 LayoutManager 的 onMeasure 控件),以及添加滚动完居中监听
     * @param recyclerView
     */
    override fun onAttachedToWindow(recyclerView: RecyclerView) {
        //设置不自动RecyclerView 不自由排版,且固定尺寸
        isAutoMeasureEnabled = false
        recyclerView.setHasFixedSize(true)
        super.onAttachedToWindow(recyclerView)
    }

    /**
     * 重新测量
     */
    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        val measuredWidth = View.MeasureSpec.getSize(widthSpec)
        var measuredHeight = View.MeasureSpec.getSize(heightSpec)
        if (0 == state.itemCount) {
            //没有元素时,让当前高度为0
            measuredHeight=0
        } else {
            //拥有元素时,取第0个条目的高度设定为基准高度
            val firstView = recycler.getViewForPosition(0)
            if (null != firstView) {
                measureChildWithMargins(firstView, 0, 0)
                measuredHeight = firstView.measuredHeight * wheelCount
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

}
