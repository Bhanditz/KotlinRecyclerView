package com.cz.recyclerlibrary.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.IRecyclerAdapter

import java.util.ArrayList
import java.util.Collections

/**
 * An abstract adapter which can be extended for RecyclerView
 * create by cz on 2016/9/24
 */
abstract class BaseViewAdapter<E>(context: Context, items: List<E>?) : RecyclerView.Adapter<BaseViewHolder>(), IRecyclerAdapter<E> {
    protected val originalItems = ArrayList<E>()
    val inflater:LayoutInflater = LayoutInflater.from(context)
    val firstItem:E?
        get() = getItem(0)
    val lastItem: E?
        get() = getItem(itemsCount - 1)
    val isEmpty: Boolean
        get() = 0 == itemsCount
    val itemsCount: Int
        get() = originalItems.size

    init { items?.let { this.originalItems.addAll(it) } }

    /**
     * 创建view对象
     * @param parent
     * *
     * @param layout
     * *
     * @return
     */
    protected fun inflateView(parent: ViewGroup?, layout: Int): View =inflater.inflate(layout, parent, false)

    abstract override fun onBindViewHolder(holder: BaseViewHolder, position: Int)

    override fun getItemCount(): Int=originalItems.size


    open fun removeItems(list: List<E>?) {
        if (null != list) {
            this.originalItems.removeAll(list)
        }
    }

    open fun removeItemsNotify(list: List<E>?) {
        if (null != list) {
            this.originalItems.removeAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 移除所有条目
     */
    open fun clear() {
        this.originalItems.clear()
    }

    /**
     * 移除所有条目
     */
    open fun clearNotify() {
        this.originalItems.clear()
        notifyItemRangeRemoved(0,itemsCount)
    }


    open fun addItem(item: E?, index: Int) {
        if (null != item) {
            this.originalItems.add(index, item)
        }
    }

    open fun addItemNotify(item: E?, index: Int) {
        if (null != item) {
            this.originalItems.add(index, item)
            notifyItemInserted(index)
        }
    }

    open fun indexOfItem(item: E?): Int {
        var index = -1
        if (null != item) {
            index = this.originalItems.indexOf(item)
        }
        return index
    }

    open fun setItem(index: Int, item: E) {
        if (index < itemCount) {
            originalItems[index] = item
        }
    }

    open fun setItemNotify(index: Int, item: E) {
        if (index < itemCount) {
            originalItems[index] = item
            notifyItemChanged(index)
        }
    }

    open fun addItem(item: E?) {
        if (null != item) {
            this.originalItems.add(item)
        }
    }

    open fun addItemNotify(item: E?) {
        if (null != item) {
            this.originalItems.add(item)
            notifyItemInserted(itemCount - 1)
        }
    }

    open fun addItems(items: List<E>?, index: Int) {
        if (null != items && !items.isEmpty()) {
            this.originalItems.addAll(index, items)
        }
    }

    open fun addItems(items: List<E>?) {
        if (null != items && !items.isEmpty()) {
            this.originalItems.addAll(items)
        }
    }

    open fun addItemsNotify(items: List<E>?, index: Int) {
        if (null != items && !items.isEmpty()) {
            val size = items.size
            this.originalItems.addAll(index, items)
            notifyItemRangeInserted(index, size)
        }
    }

    open fun addItemsNotify(items: List<E>?) {
        if (null != items && !items.isEmpty()) {
            val size = items.size
            val itemCount = itemCount
            this.originalItems.addAll(items)
            notifyItemRangeInserted(itemCount, size)
        }
    }


    open fun remove(start: Int, count: Int) {
        var index = 0
        val minCount = Math.min(originalItems.size, count)
        while (index++ < minCount) {
            originalItems.removeAt(start)
        }
    }

    open fun removeItemNotify(start: Int, count: Int) {
        var index = 0
        val minCount = Math.min(originalItems.size, count)
        while (index++ < minCount) {
            originalItems.removeAt(start)
        }
        notifyItemRangeRemoved(start, minCount)
    }

    open fun remove(item: E?) {
        if (null != item) {
            remove(originalItems.indexOf(item))
        }
    }

    open fun remove(position: Int) {
        if (position in 0..(itemsCount - 1)) {
            originalItems.removeAt(position)
        }
    }

    open fun removeNotify(position: Int) {
        if (position in 0..(itemsCount - 1)) {
            originalItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun removeNotify(item: E?) {
        val item= item ?:return
        val index=indexOfItem(item)
        if (-1<index) {
            originalItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }


    /**
     * 更新条目

     * @param e
     */
    open fun updateItem(item: E?) {
        if (null != item) {
            val index = originalItems.indexOf(item)
            if (-1 != index) {
                originalItems[index] = item
            }
        }
    }

    open fun updateItemNotify(item: E?) {
        if (null != item) {
            val index = originalItems.indexOf(item)
            if (-1 != index) {
                originalItems[index] = item
                notifyItemChanged(index)
            }
        }
    }

    open fun swapItems(items: List<E>?) {
        this.originalItems.clear()
        if (null != items) {
            this.originalItems.addAll(items)
        }
    }

    open fun swapItemsNotify(items: List<E>) {
        if (0 < itemCount) {
            clearNotify()
        }
        addItemsNotify(items)
    }

    /**
     * 获得所有条目

     * @return
     */
    override fun getItems(): List<E>{
        return this.originalItems
    }

    override fun getNullableItem(position: Int): E?{
        var item: E? = null
        if (position in 0..(itemsCount - 1)) {
            item = this.originalItems[position]
        }
        return item
    }

    override fun getItem(position: Int): E =this.originalItems[position]

    /**
     * 互换元素
     */
    open fun swapItem(oldPosition: Int, newPosition: Int) {
        Collections.swap(originalItems, oldPosition, newPosition)
    }

    open fun swapItemNotify(oldPosition: Int, newPosition: Int) {
        swapItem(oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

    //---------------------------------------------
    // kotlin adapter 运算附重载
    //---------------------------------------------
    /**
     * 重载[]
     * 示例如adapter[position]==E
     */
    operator fun get(position:Int)=this.originalItems[position]

    /**
     * 重载[i,j]
     * 示例如list=adapter[i,j]
     */
    operator fun get(start:Int,end:Int)=this.originalItems.subList(start,end)

    /**
     * 重载 in 条目是否包含在集合内
     * 示例如 item in adapter
     */
    operator fun contains(item: E): Boolean =-1 != indexOfItem(item)
    /**
     * 重载!
     * 示例如 if(!adapter)..
     *
     */
    operator fun not():Boolean=!this.originalItems.isEmpty()

    /**
     * 重载+
     * 示例如 adapter+item
     */
    operator fun plus(item:E)=addItemNotify(item)

    /**
     * 重载-
     * 示例如 adapter-item
     */
    operator fun minus(item:E)=removeNotify(item)

    /**
     * 重载 adapter[i]=item
     * 示例如 adapter-item
     */
    operator fun set(position:Int,item:E)=setItemNotify(position,item)

    /**
     * 遍历所有条目
     */
    fun forEach(closure:(E)->Unit)=originalItems.forEach(closure)



}
