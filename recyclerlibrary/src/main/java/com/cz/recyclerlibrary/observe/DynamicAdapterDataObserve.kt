package com.cz.recyclerlibrary.observe

import android.support.v7.widget.RecyclerView

import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter

/**
 * RecyclerView数据变化观察者对象
 * 动态插入数据适配器对象观察者
 */
class DynamicAdapterDataObserve(private val adapter: DynamicAdapter) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter.itemRangeInsert(positionStart, itemCount)
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChanged(getAdapterPosition(positionStart), itemCount)
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(getAdapterPosition(positionStart), itemCount, payload)
    }
    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter.itemRangeRemoved(positionStart, itemCount)
    }
    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        val realPosition = getAdapterPosition(fromPosition)
        adapter.notifyItemMoved(realPosition+fromPosition, realPosition + toPosition)
    }

    /**
     * 被包装条目真实角标为:包装的头个数,以及动态条目个数+当前位置
     */
    private fun getAdapterPosition(position: Int)=adapter.headerViewCount+adapter.getStartPosition(position) + position
}