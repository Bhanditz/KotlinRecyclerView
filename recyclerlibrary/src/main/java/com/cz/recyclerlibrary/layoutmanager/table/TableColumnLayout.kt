package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/1/21.
 */
class TableColumnLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var columnSize: SparseIntArray? = null

    fun setColumnSize(columnItem: SparseIntArray) {
        this.columnSize = columnItem
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measureWidth = 0
        var measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val childCount = childCount
        //动态设定位置
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            measureWidth += if (null != columnSize) columnSize!!.get(i) else childView.measuredWidth
            if (measureHeight < childView.measuredHeight) {
                measureHeight = childView.measuredHeight
            }
        }
        setMeasuredDimension(paddingLeft + measureWidth + paddingRight, paddingTop + measureHeight + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        var left = paddingLeft
        val top = paddingTop
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            val itemSize = if (null != columnSize) columnSize!!.get(i) else childView.measuredWidth
            val measuredHeight = childView.measuredHeight
            childView.layout(left, top, left + itemSize, top + measuredHeight)
            left += itemSize
        }
    }
}
