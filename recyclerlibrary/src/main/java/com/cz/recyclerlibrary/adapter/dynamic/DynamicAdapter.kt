package com.cz.recyclerlibrary.adapter.dynamic

import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.GridSpanCallback
import com.cz.recyclerlibrary.callback.OnItemClickListener
import com.cz.recyclerlibrary.callback.OnItemLongClickListener
import com.cz.recyclerlibrary.debugLog

/**
 * 一个可以在RecyclerView 己有的Adapter,添加任一的其他条目的Adapter对象
 * 使用装饰设计模式,无使用限制
 * like :
 * 1|2|3|
 * --4--  //0 start 0 -1
 * 5|6|7|
 * --8--  //1  start 1
 * 9|10|11|
 * item1
 * item2
 * --欢迎来到xx--
 * item3
 * Model item==2
 *
 * 难点在于,如果将随机位置添加的自定义view的位置动态计算,不影响被包装Adapter

 * @param
 */
open class DynamicAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemViewCount: Int = 0
    private var longItemListener: OnItemLongClickListener? = null
    private var itemClickListener: OnItemClickListener? = null
    val dynamicHelper: DynamicHelper = DynamicHelper(this)
    var adapter:RecyclerView.Adapter<RecyclerView.ViewHolder>?=null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
        get() = field

    init {
        this.adapter= adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>?
    }
    /**
     * 获得添加头个数
     * @return
     */
    open val headerViewCount: Int
        get() = dynamicHelper.headerViewCount

    /**
     * 获得添加底部控件个数
     * @return
     */
    open val footerViewCount: Int
        get() = dynamicHelper.footerViewCount

    /**
     * 获得添加动态view个数
     * @return
     */
    val dynamicItemCount: Int
        get() = dynamicHelper.dynamicItemCount

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    var spanCount = 1
                    val adapter=adapter
                    val isWrapperPosition = dynamicHelper.isWrapperPosition(position)
                    if (!isWrapperPosition&&null != adapter && adapter is GridSpanCallback) {
                        spanCount = adapter.getSpanSize(layoutManager, getItemPosition(position))
                    }
                    //检测条件为如果为顶/底,或者dynamic position(去掉header个数的角标位),或者子类复写isFullPosition返回true的条目,占满一列
                    return if (isWrapperPosition|| isFullPosition(position)) layoutManager.spanCount else spanCount
                }
            }
        }
    }

    /**
     * 由子类实现,决定条目是否铺满
     * @see .onViewAttachedToWindow .onAttachedToRecyclerView
     * @param position
     * @return
     */
    protected open fun isFullPosition(position: Int): Boolean =false

    open fun addHeaderView(view: View?)=dynamicHelper.addHeaderView(view)

    open fun addHeaderView(view: View?,index:Int=0)=dynamicHelper.addHeaderView(view,index)

    open fun addFooterView(view: View)= dynamicHelper.addFooterView(view)

    open fun addFooterView(view: View,index: Int)= dynamicHelper.addFooterView(view,index)

    open fun getHeaderView(index: Int): View?= dynamicHelper.getHeaderView(index)

    open fun getFooterView(index: Int): View?= dynamicHelper.getFooterView(index)

    open fun removeHeaderView(view: View?)= dynamicHelper.removeHeaderView(view)

    open fun removeHeaderView(position: Int)= dynamicHelper.removeHeaderView(position)

    open fun removeFooterView(view: View?)= dynamicHelper.removeFooterView(view)

    open fun removeFooterView(position: Int)= dynamicHelper.removeFooterView(position)

    open fun findDynamicView(@IdRes id: Int): View? = dynamicHelper.findDynamicView(id)

    fun getStartPosition(position: Int): Int= dynamicHelper.getStartPosition(position)

    fun itemRangeInsert(positionStart: Int, itemCount: Int)= dynamicHelper.itemRangeInsert(positionStart,itemCount)

    fun itemRangeRemoved(positionStart: Int, itemCount: Int)= dynamicHelper.itemRangeRemoved(positionStart,itemCount)

    fun addDynamicView(view: View?, position: Int)=dynamicHelper.addDynamicView(view,position+headerViewCount)

    fun removeDynamicView(view: View?)= dynamicHelper.removeDynamicView(view)

    fun removeDynamicView(position: Int)= dynamicHelper.removeDynamicView(position)

    fun findPosition(position: Int): Int =dynamicHelper.findPosition(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var holder: RecyclerView.ViewHolder? = null
        //根据viewType获得header/footer/dynamic条目的控件
        val view = dynamicHelper.getDynamicView(viewType)
        val adapter=adapter
        if (null != view) {
            holder = BaseViewHolder(view)
        } else if (null != adapter) {
            holder = adapter.onCreateViewHolder(parent, viewType)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapter=adapter
        if (null != adapter&& !dynamicHelper.isWrapperPosition(position)) {
            holder.itemView.setOnClickListener { v ->
                val itemPosition = holder.adapterPosition
                val startPosition = dynamicHelper.getStartPosition(itemPosition)
                val realPosition=itemPosition - headerViewCount - startPosition
                if (onItemClick(v, itemPosition,realPosition) && null != itemClickListener) {
                    itemClickListener?.onItemClick(v, itemPosition,realPosition)
                }
            }
            //被包装的正常角标为:略去顶头位置,略去当前位置前面动态条目个数
            val startPosition = dynamicHelper.getStartPosition(position)
            debugLog("onBindViewHolder:${position -headerViewCount- startPosition}")
            adapter.onBindViewHolder(holder, position -headerViewCount- startPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType:Int
        val adapter=adapter
        if (null != adapter&& !dynamicHelper.isWrapperPosition(position)) {
            //获取被包装adapter条目
            val startPosition = dynamicHelper.getStartPosition(position)
            viewType = adapter.getItemViewType(position - headerViewCount - startPosition)
        } else {
            //查找动态版条目
            viewType=dynamicHelper.getItemViewType(position)
        }
        return viewType
    }

    override fun getItemCount(): Int {
        var itemCount = dynamicHelper.itemCount
        adapter?.let { itemCount += it.itemCount }
        return itemCount
    }

    override fun getItemId(position: Int): Long =position.toLong()
    /**
     * 获得被包装子条目位置
     * @param position
     * *
     * @return
     */
    fun getItemPosition(position: Int): Int =position - headerViewCount-getStartPosition(position)
    /**
     * 获得原始Adapter的真实位置
     */
    fun getAdapterPosition(position: Int): Int =position + headerViewCount+getStartPosition(position)

    /**
     * 子类点击使用
     * @param v
     * *
     * @param position
     */
    protected open fun onItemClick(v: View, position: Int,realPosition:Int): Boolean {
        return true
    }

    /**
     * 设置条目长按点击事件
     * @param listener
     */
    open fun setOnLongItemClickListener(listener: OnItemLongClickListener) {
        this.longItemListener = listener
    }

    /**
     * 设置条目点击
     * @param listener
     */
    open fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun onItemClick(action:(View,Int,Int)->Unit) {
        this.itemClickListener= OnItemClickListener { v, position, adapterPosition -> action(v,position,adapterPosition) }
    }


}