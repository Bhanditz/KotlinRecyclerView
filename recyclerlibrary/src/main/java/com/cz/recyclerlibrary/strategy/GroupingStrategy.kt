package com.cz.recyclerlibrary.strategy

import android.support.v7.widget.RecyclerView

import com.cz.recyclerlibrary.IRecyclerAdapter
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.callback.BinaryCondition
import com.cz.recyclerlibrary.callback.Condition
import com.cz.recyclerlibrary.callback.Function

import java.util.ArrayList
import java.util.Arrays

/**
 * Created by Administrator on 2017/5/20.
 * 分组策略
 */
class GroupingStrategy<T> {
    private val adapter: IRecyclerAdapter<T>
    private val indexItems: MutableList<Int>
    private var indexArray: Array<Int>? = null
    private var binaryCondition: BinaryCondition<T>? = null
    private var condition: Condition<T>? = null


    constructor(adapter: BaseViewAdapter<T>) {
        this.adapter = adapter
        this.indexItems = ArrayList<Int>()
        registerAdapterDataObserver(adapter)
    }

    /**
     * 注册数据适配器数据监听,时时同步映射角标集
     * @param adapter
     */
    private fun registerAdapterDataObserver(adapter: BaseViewAdapter<T>) {
        //同步整个列表数据变化
        adapter.setHasStableIds(true)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                refreshIndexItems()
                adapter.notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                refreshIndexItems()
                adapter.notifyDataSetChanged()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                refreshIndexItems()
                adapter.notifyDataSetChanged()
            }

            override fun onChanged() {
                super.onChanged()
                refreshIndexItems()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                refreshIndexItems()
            }
        })
    }

    fun reduce(binaryCondition: BinaryCondition<T>): GroupingStrategy<T> {
        this.binaryCondition = binaryCondition
        refreshIndexItems()
        return this
    }

    fun reduce(condition: Condition<T>): GroupingStrategy<T> {
        this.condition = condition
        refreshIndexItems()
        return this
    }

    fun <R> map(func: Function<R,T>): List<R> {
        return indexItems
                .map { this.adapter.getItem(it) }
                .mapTo(ArrayList<R>()) { func.call(it) }
    }

    fun isGroupIndex(position: Int): Boolean {
        return 0 <= Arrays.binarySearch(getIndexArray(), position)
    }

    /**
     * 使用二分查找法,根据position找到数位中该段位的位置
     * @return
     */
    fun getGroupStartIndex(position: Int): Int {
        var index = 0
        val start = getStartIndex(position)
        if (-1 < start && start < indexItems.size) {
            index = indexItems[start]
        }
        return index
    }

    /**
     * 获得映射集的真实位置,如映射集为0 13 25 给定1 返回 13
     * @param index
     * *
     * @return
     */
    fun getOriginalIndex(index: Int): Int {
        var position = 0
        if (-1 < index && index < indexItems.size) {
            position = indexItems[index]
        }
        return position
    }

    /**
     * 获得position起始位置,如映射位置为 0 13 25  则给定14 返回 1 给定12 返回0
     * @param position
     * *
     * @return
     */
    fun getStartIndex(position: Int): Int {
        var start = 0
        var end = indexItems.size
        while (end - start > 1) {
            // 中间位置
            val middle = start + end shr 1
            // 中值
            val middleValue = indexItems[middle]
            if (position > middleValue) {
                start = middle
            } else if (position < middleValue) {
                end = middle
            } else {
                start = middle
                break
            }
        }
        return start
    }


    /**
     * 刷新定位角标位置
     */
    internal fun refreshIndexItems() {
        indexArray = null
        val condition=condition
        val binaryCondition=binaryCondition
        if (null == binaryCondition && null == condition) {
            throw NullPointerException("condition is null!")
        } else if (null != binaryCondition) {
            binaryConditionRefresh(binaryCondition,adapter.getItems())
        } else if (null != condition) {
            conditionRefresh(condition,adapter.getItems())
        }
    }

    internal fun getIndexArray(): Array<Int>? {
        if (null == indexArray) {
            indexArray = indexItems.toTypedArray()
        }
        return indexArray
    }

    internal fun binaryConditionRefresh(condition:BinaryCondition<T>,items: List<T>) {
        indexItems.clear()
        var lastItem: T? = null
        for (index in items.indices) {
            val item = items[index]
            if (null == lastItem || condition.apply(lastItem, item)) {
                indexItems.add(index)
            }
            lastItem = item
        }
    }

    internal fun conditionRefresh(condition:Condition<T>,items: List<T>) {
        indexItems.clear()
        for (index in items.indices) {
            val item = items[index]
            if (condition.apply(item)) {
                indexItems.add(index)
            }
        }
    }

    companion object {
        fun <T> of(adapter: BaseViewAdapter<T>): GroupingStrategy<T> {
            return GroupingStrategy(adapter)
        }
    }
}
