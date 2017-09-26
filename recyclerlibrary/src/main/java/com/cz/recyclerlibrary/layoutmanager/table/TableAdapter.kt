package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/9/26.
 */
abstract class TableAdapter<T>(val context: Context,items:List<T>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items= mutableListOf<T>()

    init {
        if(null!=items){
            this.items.addAll(items)
        }
    }

    override fun getItemCount(): Int =this.items.size

    abstract fun getRowView():View
    /**
     * 绑定列
     */
    abstract fun bindRowView(view: View, row:Int)

    abstract fun getColumnView():View
    /**
     * 初始化列数据
     */
    abstract fun bindColumnView(view: View,column:Int)


}