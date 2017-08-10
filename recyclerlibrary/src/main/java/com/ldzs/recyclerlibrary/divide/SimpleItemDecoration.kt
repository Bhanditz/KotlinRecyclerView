package com.ldzs.recyclerlibrary.divide

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

/**
 * Created by cz on 16/1/22.
 */
class SimpleItemDecoration : RecyclerView.ItemDecoration() {
    companion object {
        //分隔线模式
        const val VERTICAL = 0
        const val HORIZONTAL = 1
        const val GRID = 2
    }

    private var strokeWidth: Int = 0
    private var horizontalPadding: Int = 0
    private var verticalPadding: Int = 0
    private var headerCount: Int = 0
    private var footerCount: Int = 0
    private var showHeader: Boolean = false
    private var showFooter: Boolean = false
    private var drawable: Drawable? = null
    private var divideMode: Int = 0

    @IntDef(value = *longArrayOf(HORIZONTAL.toLong(), VERTICAL.toLong(), GRID.toLong()))
    annotation class Mode

    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = strokeWidth
    }

    fun setDivideHorizontalPadding(padding: Int) {
        this.horizontalPadding = padding
    }

    fun setDivideVerticalPadding(padding: Int) {
        this.verticalPadding = padding
    }

    fun showHeaderDecoration(showHeader: Boolean) {
        this.showHeader = showHeader
    }

    fun showFooterDecoration(showFooter: Boolean) {
        this.showFooter = showFooter
    }

    fun setColorDrawable(color: Int) {
        this.drawable = ColorDrawable(color)
    }


    fun setDrawable(drawable: Drawable?) {
        this.drawable = drawable
    }

    /**
     * 设置分隔线模式

     * @param mode
     */
    fun setDivideMode(@Mode mode: Int) {
        this.divideMode = mode
    }

    fun setHeaderCount(headerCount: Int) {
        this.headerCount = headerCount
    }

    fun setFooterCount(footerCount: Int) {
        this.footerCount = footerCount
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (null == drawable) return
        when (divideMode) {
            VERTICAL -> drawLinearVertical(c, parent, state)
            HORIZONTAL -> drawLinearHorizontal(c, parent, state)
            GRID -> {
                drawGridVertical(c, parent, state)
                drawGridHorizontal(c, parent, state)
            }
        }
    }


    fun drawLinearVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val itemCount = state.itemCount
        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val itemPosition = parent.getChildAdapterPosition(child)
            if (needDraw(itemCount, itemPosition)) {
                val params = child
                        .layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + strokeWidth
                drawable!!.setBounds(left + verticalPadding, top, right - verticalPadding, bottom)
                drawable!!.draw(c)
            }
        }
    }

    fun drawLinearHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        val itemCount = state.itemCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val itemPosition = parent.getChildAdapterPosition(child)
            if (needDraw(itemCount, itemPosition)) {
                val params = child
                        .layoutParams as RecyclerView.LayoutParams
                val left = child.right + params.rightMargin
                val right = left + strokeWidth
                drawable!!.setBounds(left, top + horizontalPadding, right, bottom - horizontalPadding)
                drawable!!.draw(c)
            }
        }
    }


    fun drawGridVertical(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val strokeWidth = this.strokeWidth
        val itemCount = state.itemCount
        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val itemPosition = layoutParams.viewLayoutPosition
            if (needDraw(itemCount, itemPosition)) {
                //绘左侧
                var left = child.left - layoutParams.leftMargin - strokeWidth
                var right = child.left - layoutParams.leftMargin
                var top = child.top + layoutParams.topMargin - strokeWidth
                var bottom = child.bottom + layoutParams.bottomMargin + strokeWidth
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(c)
                //绘右侧
                left = child.right + layoutParams.rightMargin
                right = child.right + layoutParams.rightMargin + strokeWidth
                top = child.top + layoutParams.topMargin - strokeWidth
                bottom = child.bottom + layoutParams.bottomMargin + strokeWidth
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(c)
            }
        }
    }

    fun drawGridHorizontal(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val strokeWidth = this.strokeWidth
        val childCount = parent.childCount
        val itemCount = state.itemCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val itemPosition = layoutParams.viewLayoutPosition
            if (needDraw(itemCount, itemPosition)) {
                //绘上边
                var left = child.left
                var right = child.right
                var top = child.top - layoutParams.topMargin - strokeWidth
                var bottom = child.top - layoutParams.topMargin
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(c)
                //绘下边
                left = child.left
                right = child.right
                top = child.bottom + layoutParams.bottomMargin
                bottom = child.bottom + layoutParams.bottomMargin + strokeWidth
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(c)
            }
        }
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val strokeWidth = this.strokeWidth
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        val itemCount = state!!.itemCount
        val itemPosition = layoutParams.viewLayoutPosition
        if (!needDraw(itemCount, itemPosition)) {
            outRect.set(0, 0, 0, 0)
        } else {
            when (divideMode) {
                VERTICAL -> outRect.set(0, 0, 0, strokeWidth)
                HORIZONTAL -> outRect.set(0, 0, strokeWidth, 0)
                GRID -> {
                    val layoutManager = parent.layoutManager
                    if (layoutManager is GridLayoutManager) {
                        val gridLayoutManager = layoutManager
                        val sizeLookup = gridLayoutManager.spanSizeLookup
                        val spanSize = sizeLookup.getSpanSize(itemPosition)
                        val spanCount = gridLayoutManager.spanCount
                        if (spanSize == spanCount) {
                            outRect.set(0, 0, 0, 0)
                            return
                        }
                    }
                    outRect.set(strokeWidth, strokeWidth, strokeWidth, strokeWidth)
                }
            }
        }
    }

    /**
     * 是否需要绘制

     * @param itemCount
     * *
     * @param itemPosition
     * *
     * @return
     */
    private fun needDraw(itemCount: Int, itemPosition: Int): Boolean {
        var result = null != drawable
        if (headerCount > itemPosition) {
            result = showHeader
        } else if (footerCount >= itemCount - itemPosition) {
            result = showFooter
        }
        return result
    }
}
