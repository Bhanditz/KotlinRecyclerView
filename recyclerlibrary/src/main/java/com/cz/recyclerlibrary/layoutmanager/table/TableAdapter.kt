package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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