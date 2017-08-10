package com.ldzs.recyclerlibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ldzs.recyclerlibrary.IRecyclerAdapter

import java.util.ArrayList
import java.util.Collections

/**
 * An abstract adapter which can be extended for RecyclerView
 * create by cz on 2016/9/24
 */
abstract class BaseViewAdapter<E>(context: Context, items: List<E>?) : RecyclerView.Adapter<BaseViewHolder>(), IRecyclerAdapter<E> {
    protected val items = ArrayList<E>()
    protected val inflater:LayoutInflater = LayoutInflater.from(context)
    val firstItem:E?
        get() = getItem(0)
    val lastItem: E?
        get() = getItem(itemsCount - 1)
    val isEmpty: Boolean
        get() = 0 == itemsCount
    val itemsCount: Int
        get() = items.size

    init { items?.let { this.items.addAll(it) } }

    /**
     * 创建view对象
     * @param parent
     * *
     * @param layout
     * *
     * @return
     */
    protected fun inflateView(parent: ViewGroup, layout: Int): View =inflater.inflate(layout, parent, false)

    abstract override fun onBindViewHolder(holder: BaseViewHolder, position: Int)

    override fun getItemCount(): Int=items.size


    open fun removeItems(list: List<E>?) {
        if (null != list) {
            this.items.removeAll(list)
        }
    }

    open fun removeItemsNotify(list: List<E>?) {
        if (null != list) {
            this.items.removeAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 移除所有条目
     */
    open fun clear() {
        this.items.clear()
    }

    /**
     * 移除所有条目
     */
    open fun clearNotify() {
        this.items.clear()
        notifyItemRangeRemoved(0,itemsCount)
    }


    open fun addItem(e: E?, index: Int) {
        if (null != e) {
            this.items.add(index, e)
        }
    }

    open fun addItemNotify(e: E?, index: Int) {
        if (null != e) {
            this.items.add(index, e)
            notifyItemInserted(index)
        }
    }

    open fun indexOfItem(e: E?): Int {
        var index = -1
        if (null != e) {
            index = this.items.indexOf(e)
        }
        return index
    }

    operator fun contains(e: E): Boolean {
        return -1 != indexOfItem(e)
    }

    open fun setItem(index: Int, e: E) {
        if (index < itemCount) {
            items[index] = e
        }
    }

    open fun setItemNotify(index: Int, e: E) {
        if (index < itemCount) {
            items[index] = e
            notifyItemChanged(index)
        }
    }

    open fun addItem(e: E?) {
        if (null != e) {
            this.items.add(e)
        }
    }

    open fun addItemNotify(e: E?) {
        if (null != e) {
            this.items.add(e)
            notifyItemInserted(itemCount - 1)
        }
    }

    open fun addItems(items: List<E>?, index: Int) {
        if (null != items && !items.isEmpty()) {
            this.items.addAll(index, items)
        }
    }

    open fun addItems(items: List<E>?) {
        if (null != items && !items.isEmpty()) {
            this.items.addAll(items)
        }
    }

    open fun addItemsNotify(items: List<E>?, index: Int) {
        if (null != items && !items.isEmpty()) {
            val size = items.size
            this.items.addAll(index, items)
            notifyItemRangeInserted(index, size)
        }
    }

    open fun addItemsNotify(items: List<E>?) {
        if (null != items && !items.isEmpty()) {
            val size = items.size
            val itemCount = itemCount
            this.items.addAll(items)
            notifyItemRangeInserted(itemCount, size)
        }
    }


    open fun remove(start: Int, count: Int) {
        var index = 0
        val minCount = Math.min(items.size, count)
        while (index++ < minCount) {
            items.removeAt(start)
        }
    }

    open fun removeNotifyItem(start: Int, count: Int) {
        var index = 0
        val minCount = Math.min(items.size, count)
        while (index++ < minCount) {
            items.removeAt(start)
        }
        notifyItemRangeRemoved(start, minCount)
    }

    open fun remove(e: E?) {
        if (null != e) {
            remove(items.indexOf(e))
        }
    }

    open fun remove(position: Int) {
        if (position in 0..(itemsCount - 1)) {
            items.removeAt(position)
        }
    }

    open fun removeNotify(position: Int) {
        if (position in 0..(itemsCount - 1)) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    /**
     * 更新条目

     * @param e
     */
    open fun updateItem(e: E?) {
        if (null != e) {
            val index = items.indexOf(e)
            if (-1 != index) {
                items[index] = e
            }
        }
    }

    open fun updateItemNotify(e: E?) {
        if (null != e) {
            val index = items.indexOf(e)
            if (-1 != index) {
                items[index] = e
                notifyItemChanged(index)
            }
        }
    }

    open fun swapItems(items: List<E>?) {
        this.items.clear()
        if (null != items) {
            this.items.addAll(items)
        }
    }

    open fun swapItemsNotify(items: List<E>) {
        val itemCount = itemCount
        if (0 != itemCount) {
            clearNotify()
        }
        addItemsNotify(items)
    }

    /**
     * 获得所有条目

     * @return
     */
    override fun getItems(): List<E>{
        return this.items
    }

    override fun getNonNullItem(position: Int): E =this.items[position]

    override fun getItem(position: Int): E? {
        var e: E? = null
        if (position in 0..(itemsCount - 1)) {
            e = this.items[position]
        }
        return e
    }
    /**
     * 互换元素
     */
    open fun swapItem(oldPosition: Int, newPosition: Int) {
        Collections.swap(items, oldPosition, newPosition)
    }

    open fun swapItemNotify(oldPosition: Int, newPosition: Int) {
        swapItem(oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

}
