package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.divide.TableItemDecoration

/**
 * Created by cz on 2017/1/20.
 * 一个表格控件
 */
class TableView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private val itemDecoration= TableItemDecoration()
    private val layoutManager=TableLayoutManager()
    private val originalPadding=IntArray(4)
    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        super.addItemDecoration(itemDecoration)
        context.obtainStyledAttributes(attrs, R.styleable.TableView).apply {
            setColumnMinWidth(getDimension(R.styleable.TableView_tv_columnMinWidth,0f))
            setColumnMaxWidth(getDimension(R.styleable.TableView_tv_columnMaxWidth,Integer.MAX_VALUE*1f))
            setDivider(getDrawable(R.styleable.TableView_tv_divider))
            setDividerSize(getDimension(R.styleable.TableView_tv_dividerSize,0f))

            setHeaderBackground(getDrawable(R.styleable.TableView_tv_headerBackground))
            setHeaderPadding(getDimension(R.styleable.TableView_tv_headerPadding,0f))
            setHeaderPaddingLeft(getDimension(R.styleable.TableView_tv_headerPaddingLeft,0f))
            setHeaderPaddingTop(getDimension(R.styleable.TableView_tv_headerPaddingTop,0f))
            setHeaderPaddingRight(getDimension(R.styleable.TableView_tv_headerPaddingRight,0f))
            setHeaderPaddingBottom(getDimension(R.styleable.TableView_tv_headerPaddingBottom,0f))
            setItemBackground(getDrawable(R.styleable.TableView_tv_itemBackground))
            setItemPadding(getDimension(R.styleable.TableView_tv_itemPadding,0f))
            setItemPaddingLeft(getDimension(R.styleable.TableView_tv_itemPaddingLeft,0f))
            setItemPaddingTop(getDimension(R.styleable.TableView_tv_itemPaddingTop,0f))
            setItemPaddingRight(getDimension(R.styleable.TableView_tv_itemPaddingRight,0f))
            setItemPaddingBottom(getDimension(R.styleable.TableView_tv_itemPaddingBottom,0f))

            recycle()
        }
        //记录当前设置padding
        setPaddingInner()
    }

    private fun setItemBackground(drawable: Drawable?) {

    }

    private fun setItemPadding(padding: Float) {

    }

    private fun setItemPaddingLeft(padding: Float) {

    }

    private fun setItemPaddingTop(padding: Float) {

    }

    private fun setItemPaddingRight(padding: Float) {

    }

    private fun setItemPaddingBottom(padding: Float) {

    }

    private fun setHeaderBackground(drawable: Drawable?) {

    }

    private fun setHeaderPadding(padding: Float) {

    }

    private fun  setHeaderPaddingLeft(padding: Float) {

    }

    private fun setHeaderPaddingTop(padding: Float) {

    }

    private fun setHeaderPaddingRight(padding: Float) {

    }

    private fun setHeaderPaddingBottom(padding: Float) {

    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setPaddingInner()
    }

    fun setOriginalPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
    }

    private fun setPaddingInner() {
        originalPadding[0] = paddingLeft
        originalPadding[1] = paddingTop
        originalPadding[2] = paddingRight
        originalPadding[3] = paddingBottom
    }


    val originalPaddingLeft:Int
        get() = originalPadding[0]

    val originalPaddingTop:Int
        get() = originalPadding[1]

    val originalPaddingRight:Int
        get() = originalPadding[2]

    val originalPaddingBottom:Int
        get() = originalPadding[3]

    /**
     * 设置条目列最小宽度
     */
    private fun setColumnMinWidth(minWidth: Float) {
        layoutManager.setColumnMinWidth(minWidth)
    }

    /**
     * 设置条目列最大宽度
     */
    private fun setColumnMaxWidth(maxWidth: Float) {
        layoutManager.setColumnMaxWidth(maxWidth)
    }

    /**
     * 设置分隔线drawable对象
     */
    private fun setDivider(drawable: Drawable?) {
        itemDecoration.setDrawable(drawable)
        layoutManager.setDrawable(drawable)
        invalidateItemDecorations()
    }

    /**
     * 设置分隔线尺寸
     */
    private fun setDividerSize(size: Float) {
        itemDecoration.setStrokeWidth(size)
        layoutManager.setStrokeWidth(size)
        invalidateItemDecorations()
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        if(adapter !is TableAdapter<*>){
            throw ClassCastException("Adapter必须继承自TableAdapter")
        } else {
            super.setLayoutManager(layoutManager)
            super.setAdapter(adapter)
        }
    }

    @Deprecated("nothing to do!", ReplaceWith("Unit"))
    override fun addItemDecoration(decor: ItemDecoration?)=Unit
    /**
     * @param layout
     */
    @Deprecated("nothing to do!", ReplaceWith("Unit"))
    override fun setLayoutManager(layout: RecyclerView.LayoutManager)=Unit


}
