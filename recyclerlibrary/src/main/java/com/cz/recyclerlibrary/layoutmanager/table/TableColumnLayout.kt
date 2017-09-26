package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.OrientationHelper
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/1/21.
 * 一个带序列号的表格列,并将计算此列内控件长/宽
 */
class TableColumnLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var columnSize: IntArray? = null
    private var orientation:Int=OrientationHelper.VERTICAL

    fun setColumnSize(columnItem: IntArray) {
        this.columnSize = columnItem
        requestLayout()
    }

    fun setOrientation(orientation:Int){
        this.orientation=orientation
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val array=columnSize
        var totalWidth = 0
        var measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        //动态设定位置
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            val measureWidth:Int
            if (null != array){
                //非首次,给定控件固定值
                measureChild(childView, MeasureSpec.makeMeasureSpec(array[i],MeasureSpec.EXACTLY), heightMeasureSpec)
                measureWidth=array[i]
            } else {
                //首次直接计算控件大小
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                measureWidth=childView.measuredWidth
            }
            totalWidth+=measureWidth
            if (measureHeight < childView.measuredHeight) {
                measureHeight = childView.measuredHeight
            }
        }
        setMeasuredDimension(paddingLeft + totalWidth + paddingRight, paddingTop + measureHeight + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        var left = paddingLeft
        val top = paddingTop
        val array=columnSize
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            val itemSize = if (null != array) array[i] else childView.measuredWidth
            val measuredHeight = childView.measuredHeight
            childView.layout(left, top, left + itemSize, top + measuredHeight)
            left += itemSize
        }
    }
}
