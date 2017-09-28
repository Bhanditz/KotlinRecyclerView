package com.cz.recyclerlibrary.divide

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.table.TableColumnLayout

/**
 * Created by cz on 16/1/22.
 */
class TableItemDecoration : RecyclerView.ItemDecoration() {
    companion object {
        //分隔线模式
        const val VERTICAL = 0
        const val HORIZONTAL = 1
    }
    private var orientation:Int=VERTICAL
    private var strokeWidth: Float = 0f
    private var drawable: Drawable? = null

    @IntDef(value = *longArrayOf(HORIZONTAL.toLong(), VERTICAL.toLong()))
    annotation class Mode

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
    }

    fun setColorDrawable(color: Int) {
        this.drawable = ColorDrawable(color)
    }

    fun setOrientation(@Mode orientation:Int){
        this.orientation=orientation
    }

    fun setDrawable(drawable: Drawable?) {
        this.drawable = drawable
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        if (null == drawable) return
        when (orientation) {
            HORIZONTAL -> drawLinearHorizontal(canvas, parent, state)
            else -> drawLinearVertical(canvas, parent, state)
        }
    }

    fun drawLinearVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        //最后一个条目不画线
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            //控件绘图不超过padding区域
            if(child.bottom>parent.paddingTop&&child.bottom<parent.height-parent.paddingBottom){
                val params = child.layoutParams as RecyclerView.LayoutParams
                val left = parent.paddingLeft
                val right = parent.width-parent.paddingRight
                val top = child.bottom + params.bottomMargin
                val bottom = top + strokeWidth.toInt()
                drawable?.setBounds(left , top, right , bottom)
                drawable?.draw(c)
            }
        }
    }

    fun drawLinearHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount-1
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val top = parent.paddingTop
            val bottom = child.height-parent.paddingBottom
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + strokeWidth.toInt()
            drawable?.setBounds(left, top , right, bottom )
            drawable?.draw(c)
        }
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        if(view !is TableColumnLayout){
            throw ClassCastException("条目根View必须是一个TableColumnLayout")
        }
        val strokeWidth = this.strokeWidth
        when (orientation) {
            HORIZONTAL -> outRect.set(0, 0, strokeWidth.toInt(), 0)
            VERTICAL -> outRect.set(0, 0, 0, strokeWidth.toInt())
            else-> outRect.set(0, 0, 0, 0)
        }
    }
}
