package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.callback.OnItemLongClickListener
import com.cz.recyclerlibrary.callback.OnTableItemClickListener
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
            setHeaderFullMode(getInt(R.styleable.TableView_tv_headerFullMode,TableLayoutManager.AUTO))
            setHeaderMinWidth(getDimension(R.styleable.TableView_tv_headerMinWidth,0f))
            setHeaderMaxWidth(getDimension(R.styleable.TableView_tv_headerMaxWidth,Integer.MAX_VALUE*1f))
            setDivider(getDrawable(R.styleable.TableView_tv_divider))
            setDividerSize(getDimension(R.styleable.TableView_tv_dividerSize,0f))
            setHeaderBackground(getDrawable(R.styleable.TableView_tv_headerBackground))
            setHeaderPadding(getDimension(R.styleable.TableView_tv_headerPadding,0f).toInt())
            setHeaderPaddingLeft(getDimension(R.styleable.TableView_tv_headerPaddingLeft,0f).toInt())
            setHeaderPaddingTop(getDimension(R.styleable.TableView_tv_headerPaddingTop,0f).toInt())
            setHeaderPaddingRight(getDimension(R.styleable.TableView_tv_headerPaddingRight,0f).toInt())
            setHeaderPaddingBottom(getDimension(R.styleable.TableView_tv_headerPaddingBottom,0f).toInt())
            setItemBackground(getDrawable(R.styleable.TableView_tv_itemBackground))
            setItemPadding(getDimension(R.styleable.TableView_tv_itemPadding,0f).toInt())
            setItemPaddingLeft(getDimension(R.styleable.TableView_tv_itemPaddingLeft,0f).toInt())
            setItemPaddingTop(getDimension(R.styleable.TableView_tv_itemPaddingTop,0f).toInt())
            setItemPaddingRight(getDimension(R.styleable.TableView_tv_itemPaddingRight,0f).toInt())
            setItemPaddingBottom(getDimension(R.styleable.TableView_tv_itemPaddingBottom,0f).toInt())
            recycle()
        }
        //记录当前设置padding
        setPaddingInner()
    }

    /**
     * 单例设置铺满
     */
    private fun setHeaderFullMode(mode: Int) {
        layoutManager.setHeaderFullMode(mode)
    }

    private fun setItemBackground(drawable: Drawable?) {
        layoutManager.setItemBackground(drawable)
    }

    private fun setItemPadding(padding: Int) {
        layoutManager.setItemPadding(padding)
    }

    private fun setItemPaddingLeft(padding: Int) {
        layoutManager.setItemPaddingLeft(padding)
    }

    private fun setItemPaddingTop(padding: Int) {
        layoutManager.setItemPaddingTop(padding)
    }

    private fun setItemPaddingRight(padding: Int) {
        layoutManager.setItemPaddingRight(padding)
    }

    private fun setItemPaddingBottom(padding: Int) {
        layoutManager.setItemPaddingBottom(padding)
    }

    private fun setHeaderBackground(drawable: Drawable?) {
        layoutManager.setHeaderBackground(drawable)
    }

    private fun setHeaderPadding(padding: Int) {
        layoutManager.setHeaderPadding(padding)
    }

    private fun  setHeaderPaddingLeft(padding: Int) {
        layoutManager.setHeaderPaddingLeft(padding)
    }

    private fun setHeaderPaddingTop(padding: Int) {
        layoutManager.setHeaderPaddingTop(padding)
    }

    private fun setHeaderPaddingRight(padding: Int) {
        layoutManager.setHeaderPaddingRight(padding)
    }

    private fun setHeaderPaddingBottom(padding: Int) {
        layoutManager.setHeaderPaddingBottom(padding)
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
    private fun setHeaderMinWidth(minWidth: Float) {
        layoutManager.setHeaderMinWidth(minWidth)
    }

    /**
     * 设置条目列最大宽度
     */
    private fun setHeaderMaxWidth(maxWidth: Float) {
        layoutManager.setHeaderMaxWidth(maxWidth)
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


    fun setOnItemClickListener(listener: OnTableItemClickListener) {
        this.layoutManager.setOnItemClickListener(listener)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.layoutManager.setOnItemLongClickListener(listener)
    }

    fun onItemClick(listener:(View,Int)->Unit){
        setOnItemClickListener(OnTableItemClickListener { v, position -> listener(v,position) })
    }

    fun onItemLongClick(listener:(View,Int)->Boolean){
        setOnItemLongClickListener(OnItemLongClickListener { v, position -> listener(v,position) })
    }

}
