package com.cz.recyclerlibrary.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter
import com.cz.recyclerlibrary.debugLog

/**
 * Created by cz on 16/1/23.
 * 固定刷新尾的数据适配器
 * 永远固定与底部的刷新尾,不允许删除,配合PullToRefreshRecyclerView使用,而HeaderAdapter则可单独使用
 * 不会影响HeaderAdapter自身逻辑
 */
open class RefreshAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : DynamicAdapter(adapter) {
    private var refreshFooterView: View? = null

    internal fun addRefreshView(view: View) {
        val insertIndex: Int
        if (null == refreshFooterView) {
            insertIndex = footerViewCount
        } else {
            insertIndex = footerViewCount - 1
        }
        refreshFooterView=view
        debugLog("addRefreshView:$view")
        super.addFooterView(view, insertIndex)
    }

    internal fun removeRefreshView(view:View){
        if(refreshFooterView==view){
            refreshFooterView=null
        }
        super.removeFooterView(view)
    }

    override fun addFooterView(view: View) {
        if(null==refreshFooterView){
            super.addFooterView(view)
        } else {
            super.addFooterView(view,footerViewCount-1)
        }
    }

    override fun addFooterView(view: View, index: Int) {
        debugLog("addFooterView:$view")
        if(null==refreshFooterView){
            //如果没有添加尾,直接添加
            super.addFooterView(view, index)
        } else if(index==footerViewCount){
            //如果己添加刷新尾,自动添加到刷新尾后面
            super.addFooterView(view, index-1)
        }
    }

    /**
     * 移除指定的FooterView对象,不能移除refreshFooterView
     * @param view
     */
    override fun removeFooterView(view: View?) {
        if (null != view && view != refreshFooterView){
            super.removeFooterView(view)
        }
    }

    /**
     * 移除指定的FooterView对象
     * @param position
     */
    override fun removeFooterView(position: Int) {
        val footerView = getFooterView(position)
        if(null!=footerView){
            super.removeFooterView(position)
        }
    }

}
