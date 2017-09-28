package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.OrientationHelper
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cz.recyclerlibrary.debugLog
import java.util.*

/**
 * Created by cz on 2017/1/21.
 * 一个带序列号的表格列,并将计算此列内控件长/宽
 */
class TableColumnLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var columnSize: IntArray? = null
    private var orientation:Int=OrientationHelper.HORIZONTAL
    private var dividerDrawable:Drawable?=null
    private var dividerSize=0f

    init {
        setWillNotDraw(false)
    }

    fun setColumnSize(columnItem: IntArray) {
        this.columnSize = columnItem
    }

    fun setOrientation(orientation:Int){
        this.orientation=orientation
    }

    fun setDividerDrawable(drawable:Drawable?){
        this.dividerDrawable=drawable
    }

    fun setDividerSize(dividerSize:Float){
        this.dividerSize=dividerSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalWidth = 0
        var measureHeight = 0
        val array=columnSize
        //动态设定位置
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            val measureWidth:Int
            if (null != array){
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
        val array = columnSize
        for (i in 0..childCount - 1) {
            val childView = getChildAt(i)
            val itemSize = if (null != array) array[i] else childView.measuredWidth
            val measuredHeight = childView.measuredHeight
            childView.layout(left, top, left + itemSize, top + measuredHeight)
            left += itemSize
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(i in 0..childCount - 1){
            val childView=getChildAt(i)
            if (OrientationHelper.HORIZONTAL == orientation) {
                drawDividerDrawable(canvas,dividerDrawable,childView.right.toFloat(),0f,childView.right+dividerSize,height*1f)
            } else if (OrientationHelper.VERTICAL == orientation) {
                drawDividerDrawable(canvas,dividerDrawable,0f,childView.bottom.toFloat(),width*1f,childView.bottom+dividerSize)
            }
        }
    }

    /**
     * 根据给定位置,绘制drawable
     * @param canvas
     * @param drawable
     * @param left 绘制左边点
     * @param top 绘制上边点
     * @param right 绘制右边点
     * @param bottom 绘制下边点
     */
    private fun drawDividerDrawable(canvas: Canvas, drawable: Drawable?, left:Float, top:Float, right:Float, bottom:Float){
        drawable?.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        drawable?.draw(canvas)
    }
}
