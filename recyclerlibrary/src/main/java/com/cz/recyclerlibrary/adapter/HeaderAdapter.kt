package com.cz.recyclerlibrary.adapter

import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.tree.TreeAdapter
import com.cz.recyclerlibrary.callback.OnItemClickListener

import java.util.ArrayList

/**
 * 包装RecyclerView的数据适配器,添加头和尾操作
 * 利用adapter的分类达到
 */
open class HeaderAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_HEADER = -1//从-1起始开始减
    private val TYPE_NORMAL = 0//默认从0开始
    private val TYPE_NORMAL_ITEM_COUNT = 1 shl 4//随意取的值,确保装饰Adapter对象不会超过此界即可
    private val TYPE_FOOTER = TYPE_NORMAL_ITEM_COUNT + 1
    private val headerViews: ArrayList<HeaderViewItem>
    private val footViews: ArrayList<HeaderViewItem>
    private var headerCount: Int = 0
    private var footerCount: Int = 0//头/尾的总个数
    private var listener: OnItemClickListener? = null
    var adapter:RecyclerView.Adapter<RecyclerView.ViewHolder>?=null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
        get() = field

    init {
        this.adapter= adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>?
        this.headerViews = ArrayList<HeaderViewItem>()
        this.footViews = ArrayList<HeaderViewItem>()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView!!.layoutManager
        if (manager is GridLayoutManager) {
            val gridManager = manager
            gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeader(position) || isFooter(position)) gridManager.spanCount else 1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        val lp = holder!!.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))) {
            lp.isFullSpan = true
        }
    }

    fun isHeader(position: Int): Boolean {
        return position >= 0 && position < headerViews.size
    }

    fun isFooter(position: Int): Boolean {
        val itemCount = itemCount
        return position < itemCount && position >= itemCount - footViews.size
    }

    open fun addHeaderView(view: View?) {
        val view=view?:return
        val viewType = TYPE_HEADER - headerCount
        val index = headerViews.size
        this.headerViews.add(index, HeaderViewItem(viewType, view))
        notifyItemInserted(index)
        headerCount++
        if (null != adapter) {
            //避免包装子条目混乱
            if (adapter is TreeAdapter<*>) {
                val treeAdapter = this.adapter as TreeAdapter<*>
                treeAdapter.setHeaderCount(headersCount)
            }
        }
    }

    /**
     * 此方法不开放,避免角标混乱

     * @param view
     * *
     * @param index
     */
    protected open fun addFooterView(view: View, index: Int) {
        val viewType = TYPE_FOOTER + footerCount
        this.footViews.add(index, HeaderViewItem(viewType, view))//越界处理
        notifyItemInserted(footerStartIndex + index)
        footerCount++
    }

    open fun addFooterView(view: View) {
        addFooterView(view, footViews.size)
    }

    val headersCount: Int
        get() = headerViews.size

    val footersCount: Int
        get() = footViews.size

    /**
     * 底部组起始位置
     * @return
     */
    val footerStartIndex: Int
        get() =headersCount + (adapter?.itemCount?:0)

    /**
     * 获得指定位置的headerView
     * @param index
     * *
     * @return
     */
    fun getHeaderView(index: Int): View? {
        var view: View? = null
        if (index in 0..headerViews.size-1) {
            view = headerViews[index].view
        }
        return view
    }

    /**
     * 获得指定的位置的footerView

     * @param index
     * *
     * @return
     */
    fun getFooterView(index: Int): View? {
        var view: View? = null
        if (index in 0..footViews.size-1) {
            view = footViews[index].view
        }
        return view
    }

    /**
     * 移除指定的HeaderView对象

     * @param view
     */
    open fun removeHeaderView(view: View?) =removeHeaderView(headerViews.indexOfFirst { it.view == view })

    /**
     * 移除指定的HeaderView对象
     * @param position
     */
    open fun removeHeaderView(position: Int) {
        if(position in 0..headerViews.size-1){
            headerViews.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * 移除指定的HeaderView对象

     * @param view
     */
    open fun removeFooterView(view: View?) =removeFooterView(footViews.indexOfFirst { it.view==view })

    /**
     * 移除指定的HeaderView对象

     * @param position
     */
    open fun removeFooterView(position: Int) {
        if (position in 0..footViews.size-1) {
            footViews.removeAt(position)
            notifyItemRemoved(footerStartIndex + position)
        }
    }

    fun findHeaderFooterView(@IdRes id:Int):View?{
        var findView: View? = null
        headerViews.forEach {
            findView=it.view.findViewById(id)
            if(null!=findView) return@forEach
        }
        footViews.forEach {
            findView=it.view.findViewById(id)
            if(null!=findView) return@forEach
        }
        return findView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val holder: RecyclerView.ViewHolder?
        if (TYPE_NORMAL > viewType) {
            holder = BaseViewHolder(headerViews.find { it.viewType==viewType }?.view)
        } else if (TYPE_NORMAL_ITEM_COUNT < viewType) {
            holder = BaseViewHolder(footViews.find { it.viewType==viewType }?.view)
        } else {
            holder = adapter?.onCreateViewHolder(parent, viewType)
        }
        return holder
    }


    /**
     * ----- 1
     * ----- 2
     * item1
     * item2
     * ----- footer1
     * ----- footer2
     * @param holder
     * *
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var position = position
        if (!isHeader(position)) {
            position -= headersCount
            if (null != adapter && position < adapter?.itemCount?:0) {
                adapter?.onBindViewHolder(holder, position)
                if (adapter is BaseViewAdapter<*> || adapter is CursorRecyclerAdapter<*>) {
                    holder.itemView.setOnClickListener { v ->
                        val itemPosition = holder.adapterPosition - headersCount
                        if (onItemClick(v, itemPosition) && null != listener) {
                            listener!!.onItemClick(v, itemPosition)
                        }
                    }
                }
            }
        }
    }

    /**
     * 子类点击使用

     * @param v
     * *
     * @param position
     */
    protected open fun onItemClick(v: View, position: Int): Boolean {
        return true
    }

    override fun getItemCount(): Int {
        var itemCount = headersCount + footersCount
        if (null != adapter) {
            itemCount += adapter!!.itemCount
        }
        return itemCount
    }


    override fun getItemViewType(position: Int): Int {
        var itemType = TYPE_NORMAL
        if (isHeader(position)) {
            itemType = headerViews[position].viewType//头
        } else if (isFooter(position)) {
            itemType = footViews[footersCount - (itemCount - position)].viewType //尾
        } else {
            //子条目类型
            val itemPosition = position - headersCount
            if (adapter != null) {
                val adapterCount = adapter!!.itemCount
                if (itemPosition < adapterCount) {
                    itemType = adapter!!.getItemViewType(itemPosition)
                }
            }
        }
        return itemType
    }

    override fun getItemId(position: Int): Long =position.toLong()

    /**
     * 设置条目点击
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun onItemClick(action:(View,Int)->Unit) {
        this.listener=object :OnItemClickListener{
            override fun onItemClick(v: View, position: Int) {
                action(v,position)
            }
        }
    }

    class HeaderViewItem(val viewType: Int, val view: View)

}