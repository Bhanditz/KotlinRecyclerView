package com.cz.recyclerlibrary.observe

import android.support.v7.widget.RecyclerView

import com.cz.recyclerlibrary.adapter.drag.DynamicAdapter

/**
 * RecyclerView数据变化观察者对象
 * 动态插入数据适配器对象观察者
 */
class DynamicAdapterDataObserve(private val adapter: DynamicAdapter) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter.itemRangeInsert(adapter.getStartIndex(positionStart) + positionStart, itemCount)
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChanged(adapter.getStartIndex(positionStart) + positionStart, itemCount)
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(adapter.getStartIndex(positionStart) + positionStart, itemCount, payload)
    }
    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter.itemRangeRemoved(adapter.getStartIndex(positionStart) + positionStart, itemCount)
    }
    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapter.notifyItemMoved(adapter.getStartIndex(fromPosition) + fromPosition, adapter.getStartIndex(toPosition) + toPosition)
    }
}