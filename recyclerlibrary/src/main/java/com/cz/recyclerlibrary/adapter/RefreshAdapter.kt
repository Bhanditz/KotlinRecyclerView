package com.cz.recyclerlibrary.adapter

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.drag.DynamicAdapter

import java.util.ArrayList

/**
 * Created by cz on 16/1/23.
 * 固定刷新尾的数据适配器
 * 永远固定与底部的刷新尾,不允许删除,配合PullToRefreshRecyclerView使用,而HeaderAdapter则可单独使用
 * 不会影响HeaderAdapter自身逻辑
 */
open class RefreshAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : DynamicAdapter(adapter) {
    private var refreshFooterView: View? = null

    fun addRefreshView(view: View) {
        val insertIndex: Int
        if (null == refreshFooterView) {
            insertIndex = footersCount
        } else {
            insertIndex = footersCount - 1
        }
        addFooterView(view, insertIndex)
    }

    fun removeRefreshView(view:View) =super.removeFooterView(view)

    /**
     * 移除指定的HeaderView对象
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

    /**
     * 移除指定位置的header view
     * @param position
     */
    override fun removeHeaderView(position: Int) {
        if (0 in 0..itemPositions.size) {
            removeDynamicView(itemPositions[position])
        }
    }
}
