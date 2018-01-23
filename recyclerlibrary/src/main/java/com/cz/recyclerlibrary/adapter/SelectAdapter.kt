package com.cz.recyclerlibrary.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

import com.cz.recyclerlibrary.PullToRefreshRecyclerView
import com.cz.recyclerlibrary.callback.Selectable

import java.util.ArrayList

/**
 * Created by cz on 4/3/16.
 * 一个可设置选择模式的数据乱配器
 */
open class SelectAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : RefreshAdapter(adapter) {
    companion object {
        val MAX_COUNT = Integer.MAX_VALUE
        val INVALID_POSITION = -1
        // 三种选择状态
        val CLICK = 0//单击
        val SINGLE_SELECT = 1//单选
        val MULTI_SELECT = 2//多选
        val RECTANGLE_SELECT = 3//块选择
    }

    private var singleSelectListener: PullToRefreshRecyclerView.OnSingleSelectListener? = null
    private var multiSelectListener: PullToRefreshRecyclerView.OnMultiSelectListener? = null
    private var rectangleSelectListener: PullToRefreshRecyclerView.OnRectangleSelectListener? = null
    private var selectPosition: Int = -1// 选中位置
    private var selectMaxCount: Int = 0
    private var start: Int = INVALID_POSITION
    private var end: Int = INVALID_POSITION//截选范围
    private var mode: Int = 0//选择模式
    var multiSelectItems = ArrayList<Int>()//选中集
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    /*
     * 设置选择模式
     *
     * @param newMode
     */
    fun setSelectMode(newMode: Int) {
        //清空所有其他状态
        when (this.mode) {
            SINGLE_SELECT -> {
                //清除单选择状态
                val lastSelectPosition = selectPosition
                selectPosition = INVALID_POSITION
                if (INVALID_POSITION != lastSelectPosition) {
                    notifyItemChanged(lastSelectPosition + headerViewCount)
                }
            }
            MULTI_SELECT -> {
                multiSelectItems.let {
                    it.forEach { notifyItemChanged(it + headerViewCount) }
                    it.clear()
                }
            }
            RECTANGLE_SELECT -> {
                val s = Math.min(start, end) + headerViewCount
                val e = Math.max(start, end) + headerViewCount
                notifyItemRangeChanged(s, e - s + 1)
                start = 0
                end = 0
            }
        }
        start = INVALID_POSITION
        end = INVALID_POSITION
        selectPosition=INVALID_POSITION
        multiSelectItems.clear()
        this.mode = newMode
    }

    fun setSelectMaxCount(count: Int) {
        this.selectMaxCount = count
    }

    //如果在正常范围内
    //删掉上一个选择的条目
    var singleSelectPosition: Int
        get() = this.selectPosition
        set(position) {
            val lastPosition = selectPosition
            this.selectPosition = position
            if(lastPosition in 0..(itemCount-1)){
                notifyItemChanged(lastPosition)
            }
            if (position in 0..(itemCount - 1)) {
                notifyItemChanged(position)
            }
        }

    val rectangleSelectPosition: IntRange
        get() = IntRange(start, end)

    fun setRectangleSelectPosition(start: Int, end: Int) {
        this.start = start
        this.end = end
        notifyItemRangeChanged(start, end - start)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val headersCount = headerViewCount
        val footerViewCount = footerViewCount
        when (mode) {
            SINGLE_SELECT -> selectPosition(holder, position, headersCount, footerViewCount, selectPosition + headersCount == position)
            MULTI_SELECT -> selectPosition(holder, position, headersCount, footerViewCount, multiSelectItems.contains(position - headersCount))
            RECTANGLE_SELECT -> {
                val s = Math.min(start + headersCount, end + headersCount)
                val e = Math.max(start + headersCount, end + headersCount)
                selectPosition(holder, position, headersCount, footerViewCount, s <= position && e >= position)
            }
            else -> selectPosition(holder, position, headersCount, footerViewCount, false)
        }
    }

    protected open fun selectPosition(holder: RecyclerView.ViewHolder, position: Int, headerCount: Int, footerCount: Int, select: Boolean) {
        val itemCount = itemCount
        if (null != adapter && adapter is Selectable<*> && position >= headerCount && position < itemCount - footerCount) {
            val selectable = adapter as Selectable<RecyclerView.ViewHolder>
            selectable.onSelectItem(holder, position - headerCount, select)
        }
    }


    override fun onItemClick(v: View, position: Int,realPosition:Int): Boolean {
        super.onItemClick(v, position,realPosition)
        when (mode) {
            MULTI_SELECT -> {
                var lastSize = multiSelectItems.size
                if (multiSelectItems.contains(realPosition)) {
                    lastSize--
                    multiSelectItems.remove(Integer.valueOf(realPosition))
                    notifyItemChanged(position)
                } else if (multiSelectItems.size < selectMaxCount) {
                    multiSelectItems.add(Integer.valueOf(realPosition))
                    notifyItemChanged(position )
                }
                multiSelectListener?.onMultiSelect(v, multiSelectItems, lastSize, selectMaxCount)
            }
            RECTANGLE_SELECT -> if (INVALID_POSITION != start && INVALID_POSITION != end) {
                notifyItemRangeChanged(Math.min(getAdapterPosition(start), getAdapterPosition(end)), Math.max(getAdapterPosition(start), getAdapterPosition(end)))
                end = INVALID_POSITION
                start = end//重置
            } else if (INVALID_POSITION == start) {
                start = realPosition
                notifyItemChanged(position)
            } else if (INVALID_POSITION == end) {
                end = realPosition
                rectangleSelectListener?.onRectangleSelect(start, end)
                notifyItemRangeChanged(Math.min(getAdapterPosition(start), getAdapterPosition(end)), Math.max(getAdapterPosition(start), getAdapterPosition(end)))
            }
            SINGLE_SELECT -> {
                val last = selectPosition
                selectPosition = realPosition
                singleSelectListener?.onSingleSelect(v, realPosition, last)
                if (0 <= selectPosition && INVALID_POSITION != last) {
                    notifyItemChanged(getAdapterPosition(last))//通知上一个取消
                }
                notifyItemChanged(position)//本次选中
            }
        }
        return CLICK == mode
    }

    /*
     * 设置单选选择监听
     *
     * @param singleSelectListener
     */
    fun setOnSingleSelectListener(singleSelectListener: PullToRefreshRecyclerView.OnSingleSelectListener) {
        this.singleSelectListener = singleSelectListener
    }

    /*
    * 设置多选选择监听
    *
    * @param singleSelectListener
    */
    fun setOnMultiSelectListener(multiSelectListener: PullToRefreshRecyclerView.OnMultiSelectListener) {
        this.multiSelectListener = multiSelectListener
    }

    /*
    * 设置截取选择监听
    *
    * @param singleSelectListener
    */
    fun setOnRectangleSelectListener(rectangleSelectListener: PullToRefreshRecyclerView.OnRectangleSelectListener) {
        this.rectangleSelectListener = rectangleSelectListener
    }

    /**
     * 列表单选
     */
    fun onSingleSelect(listener:(View,Int,Int)->Unit){
        this.singleSelectListener=object :PullToRefreshRecyclerView.OnSingleSelectListener{
            override fun onSingleSelect(v: View, newPosition: Int, oldPosition: Int) {
                listener(v,newPosition,oldPosition)
            }
        }
    }

    /**
     * 列表多选事件
     */
    fun onMultiSelect(listener:(View,ArrayList<Int>, Int,Int)->Unit) {
        this.multiSelectListener=object : PullToRefreshRecyclerView.OnMultiSelectListener {
            override fun onMultiSelect(v: View, selectPositions: ArrayList<Int>, lastSelectCount: Int, maxCount: Int) {
                listener(v, selectPositions, lastSelectCount, maxCount)
            }
        }
    }

    /**
     * 块选选中事件
     */
    fun onRectangleSelect(listener:(Int,Int)->Unit){
        this.rectangleSelectListener=object :PullToRefreshRecyclerView.OnRectangleSelectListener{
            override fun onRectangleSelect(startPosition: Int, endPosition: Int) {
                listener(startPosition,endPosition)
            }
        }
    }

}
