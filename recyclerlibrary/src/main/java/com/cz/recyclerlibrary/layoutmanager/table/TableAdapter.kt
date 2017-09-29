package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.adapter.BaseViewHolder

/**
 * Created by cz on 2017/9/26.
 */
abstract class TableAdapter<T>(val context: Context,items:List<T>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflation=LayoutInflater.from(context)
    val items= mutableListOf<T>()
    init {
        if(null!=items){
            this.items.addAll(items)
        }
    }
    protected fun inflateView(parent: ViewGroup?, layout: Int): View =layoutInflation.inflate(layout, parent, false)

    open fun getItem(position: Int): T =this.items[position]

    override fun getItemCount(): Int =this.items.size

    override fun getItemViewType(position: Int): Int =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=BaseViewHolder(TableColumnLayout(context))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val columnCount=getColumnCount()
        val layout = holder.itemView as TableColumnLayout
        layout.setColumnCount(columnCount)
        if(!layout.layoutComplete()){
            for (i in 0..columnCount - 1) {
                //获得子条目
                val itemView=getItemView(layout,position,i)
                layout.addView(itemView)
                //绑定数据
                onBindItemView(layout,itemView,position,i)
            }
        } else {
            for (i in 0..layout.childCount - 1) {
                //获得子条目
                val childView=layout.getChildAt(i)
                //绑定数据
                onBindItemView(layout,childView,position,i)
            }
        }
        //绑定外层条目布局
        onBindLayout(layout)
    }

    /**
     * 获得列数
     */
    abstract fun getColumnCount():Int

    /**
     * 绑定布局操作
     */
    open fun onBindLayout(layout:TableColumnLayout)=Unit
    /**
     * 获得条目view
     */
    abstract fun getItemView(parent:TableColumnLayout,row:Int,column:Int):View

    /**
     * 绑定条目数据
     */
    abstract fun onBindItemView(parent:TableColumnLayout,view:View,row:Int,column:Int)
    /**
     * 绑定header布局数据
     */
    open fun onBindHeaderView(headerLayout:TableColumnLayout)=Unit

    abstract fun getHeaderItemView(headerLayout:TableColumnLayout,index:Int):View
    /**
     * 初始化行数据
     */
    abstract fun onBindHeaderItemView(headerLayout:TableColumnLayout, view: View, column:Int)


}