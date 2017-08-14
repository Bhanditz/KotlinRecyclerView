package com.cz.recyclerlibrary.adapter.expand

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.OnExpandItemClickListener
import com.cz.recyclerlibrary.debugLog

import java.util.LinkedHashMap

/**
 * Created by cz on 16/1/22.
 * 一个可展开的RecyclerView数据适配器
 */
abstract class ExpandAdapter<K, E> @JvmOverloads constructor(context: Context,
                                                             items: MutableList<Entry<K,List<E>>>?,
                                                             expand: Boolean = false) : RecyclerView.Adapter<BaseViewHolder>() {
    companion object {
        private val HEADER_ITEM = 0//标题分类
        private val CHILD_ITEM = 1//条目分类
        private fun <K, E> swapEntity(items: MutableMap<K, List<E>>,expand: Boolean = false): MutableList<Entry<K,List<E>>>{
            return ArrayList<Entry<K, List<E>>>().apply {
                items.forEach {add(Entry(it.key, ArrayList(it.value),expand)) }
            }
        }
    }
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var listener: OnExpandItemClickListener? = null
    private val item=ExpandItem<K,E>()
    private var headerCount: Int = 0//顶部view总数

    @JvmOverloads constructor(context: Context, items: LinkedHashMap<K, List<E>>, expand: Boolean = false) : this(context, swapEntity(items), expand)

    init {
        item.updateItems(items,expand)
        registerAdapterDataObserver(ExpandAdapterDataObserver())
    }


    fun setHeaderViewCount(count: Int) {
        this.headerCount = count
    }

    /**
     * 获得分类个数

     * @return
     */
    val groupCount: Int
        get() = item.size

    /**
     * 获得子分类个数

     * @param groupPosition
     * *
     * @return
     */
    fun getChildrenCount(groupPosition: Int): Int {
        return item[groupPosition].items.size
    }

    /**
     * 获得分组对象

     * @param groupPosition
     * *
     * @return
     */
    fun getGroup(groupPosition: Int): K {
        return item[groupPosition].k
    }

    fun getGroupItems(groupPosition: Int): List<E> {
        return item[groupPosition].items
    }

    fun getChild(groupPosition: Int, childPosition: Int): E {
        return getGroupItems(groupPosition)[childPosition]
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        debugLog("position:" + position)
        val viewType = getItemViewType(position)
        val groupPosition = getGroupPosition(position)
        when (viewType) {
            HEADER_ITEM -> {
                onBindGroupHolder(holder, groupPosition)
                holder.itemView.setOnClickListener {
                    //展开.或关闭条目列
                    val newPosition = holder.adapterPosition - headerCount
                    debugLog("newPosition:" + newPosition)
                    val newGroupPosition = getGroupPosition(newPosition)
                    val expand = item[newGroupPosition].expand
                    item[newGroupPosition].expand = !expand//状态置反
                    onGroupExpand(holder, !expand, newGroupPosition)
                    expandGroup(newPosition, newGroupPosition, expand)
                }
            }
            CHILD_ITEM -> {
                val childPosition = getChildPosition(position)
                onBindChildHolder(holder, groupPosition, childPosition)
                holder.itemView.setOnClickListener { v ->
                    if (null != listener) {
                        listener!!.onItemClick(v, groupPosition, childPosition)
                    }
                }
            }
        }
    }

    /**
     * 当组展开或关闭时回用
     */
    protected open fun onGroupExpand(holder: BaseViewHolder, expand: Boolean, groupPosition: Int) {
        // 由子类填写,用于局部更新标题状态等
    }

    /**
     * 展开组

     * @param expand
     */
    private fun expandGroup(position: Int, groupPosition: Int, expand: Boolean) {
        val childItems = getGroupItems(groupPosition)//关闭
        val expandCount = childItems?.size
        //更新各节点起始位置,更新各节点个数
        if (expand) {
            notifyItemRangeRemoved(position + 1, expandCount)
        } else {
            notifyItemRangeInserted(position + 1, expandCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var holder: BaseViewHolder
        when (viewType) {
            HEADER_ITEM -> holder = createGroupHolder(parent)
            else -> holder = createChildHolder(parent)
        }
        return holder
    }

    /**
     * 创建组视图对象

     * @param parent
     * *
     * @return
     */
    abstract fun createGroupHolder(parent: ViewGroup): BaseViewHolder

    /**
     * 创建子视图对象

     * @param parent
     * *
     * @return
     */
    abstract fun createChildHolder(parent: ViewGroup): BaseViewHolder

    /**
     * 绑字group视图数据

     * @param holder
     * *
     * @param groupPosition
     * *
     * @return
     */
    abstract fun onBindGroupHolder(holder: BaseViewHolder, groupPosition: Int)

    /**
     * 绑定子视图数据
     * @param holder
     * *
     * @param position
     * *
     * @return
     */
    abstract fun onBindChildHolder(holder: BaseViewHolder, groupPosition: Int, position: Int)

    override fun getItemCount(): Int =item.itemCount()

    override fun getItemViewType(position: Int): Int {
        //根据快速滑动角位置,设置左边指示位置,使用二分查找
        val findPosition = getSelectPosition(position)
        val stepPosition = item.itemSteps[findPosition]
        var viewType = HEADER_ITEM
        if (0 < position - stepPosition) {
            viewType = CHILD_ITEM
        }
        return viewType
    }

    /**
     * 获得当前位置下分组位置

     * @param position
     * *
     * @return
     */
    private fun getGroupPosition(position: Int): Int {
        return getSelectPosition(position)
    }

    /**
     * 获得子孩子当前分组位置

     * @param position
     * *
     * @return
     */
    private fun getChildPosition(position: Int): Int {
        val findPosition = getSelectPosition(position)
        val stepPosition = item.itemSteps[findPosition]
        return position - stepPosition - 1
    }

    /**
     * 指定默认展开形式添加组

     * @param item
     * *
     * @param items
     * *
     * @param index
     * *
     * @param expand
     */
    open fun addGroupItems(i: K, items: List<E>, index: Int = groupCount, expand: Boolean = false) {
        val groupCount = groupCount//原来组个数
        item.items.add(index, Entry(i, ArrayList(items),expand))
        val itemSize = if (!expand) 0 else items.size//添加个数
        val startIndex: Int //计算起始位置
        if (0 == index) {
            startIndex = 0//第一个
        } else if (index == groupCount) {
            startIndex = itemCount //最后一个
        } else {
            startIndex = item.itemSteps[index]//中间
        }
        notifyItemRangeInserted(startIndex, itemSize + 1)
    }

    /**
     * 移除一个大分组
     * @param groupPosition
     */
    open fun removeGroup(groupPosition: Int) {
        val startIndex = item.itemSteps.removeAt(groupPosition)//起始位置
        val entry = item.items.removeAt(groupPosition)
        var itemCount = if(entry.expand) entry.items.size+1 else 1//子孩子个数
        notifyItemRangeRemoved(startIndex,  itemCount)
    }

    /**
     * 移除大分组内子条目

     * @param groupPosition
     * *
     * @param childPosition
     */
    open fun removeGroup(groupPosition: Int, childPosition: Int) {
        val entry = item[groupPosition]
        if(null!=entry){
            entry.items.removeAt(childPosition)
            if (entry.expand) {
                val startIndex = item.itemSteps[groupPosition] + 1//位置从大分组+1开始算
                val removePosition = startIndex + childPosition//移除的位置
                notifyItemRemoved(removePosition)
            }
        }
    }

    open fun swapItems(items: LinkedHashMap<K, List<E>>) {
        swapItems(swapEntity(items))
    }

    /**
     * 置换数据
     * @param newItems
     */
    open fun swapItems(newItems: MutableList<Entry<K, List<E>>>?, expand: Boolean = false) {
        if(0!=item.size){
            item.clear()
            notifyItemRangeRemoved(0,item.size)
        }
        newItems?.forEach { item+=Entry(it.k,ArrayList(it.items),expand) }
    }


    /**
     * 创建view对象
     * @param parent
     * *
     * @param layout
     * *
     * @return
     */
    protected fun inflateView(parent: ViewGroup, layout: Int): View=layoutInflater.inflate(layout, parent, false)

    open fun setOnExpandItemClickListener(listener: OnExpandItemClickListener) {
        this.listener = listener
    }

    /**
     * 设置闭包的点击回调对象
     */
    open fun onExpandItemClick(action:(View,Int, Int)->Unit){
        this.listener=object :OnExpandItemClickListener{
            override fun onItemClick(v: View, groupPosition: Int, childPosition: Int) {
                action(v,groupPosition,childPosition)
            }
        }
    }

    /**
     * 设置展开状态

     * @param expand
     */
    open fun setExpand(expand: Boolean) {
        item.forEach { it.expand=expand }//展开所有
        notifyDataSetChanged()
    }

    /**
     * 获得当前分组展开状态

     * @param position
     * *
     * @return
     */
    fun getGroupExpand(position: Int): Boolean =item[position].expand


    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置
     * @return
     */
    fun getSelectPosition(firstVisiblePosition: Int): Int {
        var start = 0
        val positions=item.itemSteps.toTypedArray()
        var end = positions.size
        while (end - start > 1) {
            // 中间位置
            val middle = start + end shr 1
            // 中值
            val middleValue = positions[middle]
            if (firstVisiblePosition > middleValue) {
                start = middle
            } else if (firstVisiblePosition < middleValue) {
                end = middle
            } else {
                start = middle
                break
            }
        }
        return start
    }
    /**
     * group组对象
     * @param <K>
     * *
     * @param <E>
    </E></K> */
    class Entry<out K,out E>(val k: K, val items:E,var expand:Boolean)

    inner class ExpandItem<K,E>{
        val items= ArrayList<Entry<K, ArrayList<E>>>()//数据集
        val itemSteps=ArrayList<Int>()//每个分类段个数

        fun updateItems(newItems: MutableList<Entry<K,List<E>>>?,expand:Boolean=false){
            //包装对象
            newItems?.forEach { items.add(Entry(it.k,ArrayList(it.items),expand)) }
            update()
        }
        /**
         * 更新组信息
         */
        fun update() {
            var total = 0
            itemSteps.clear()
            items.forEach { entry ->
                val childItems = entry.items
                //记录初始个数
                itemSteps.add(total)//记录每个阶段总个数
                val itemSize = if (!entry.expand) 1 else childItems.size+1
                total += itemSize
            }
            debugLog("$itemSteps")
        }

        fun itemCount():Int{
            var totalCount = 0
            items.forEach { entry ->
                if (!entry.expand) {
                    totalCount++
                } else {
                    totalCount += entry.items.size+1
                }
            }
            return totalCount
        }

        fun forEach(action: (Entry<K, ArrayList<E>>) -> Unit)=items.forEach(action)

        fun clear(){
            items.clear()
            itemSteps.clear()
            notifyDataSetChanged()
        }

        val size:Int
            get() =items.size

        operator fun get(position:Int)= items[position]

        operator fun plusAssign(item:Entry<K, ArrayList<E>>){
            items.add(item)
        }

        operator fun minusAssign(item:Entry<K, ArrayList<E>>){
            items.remove(item)
        }
    }

    inner class ExpandAdapterDataObserver:RecyclerView.AdapterDataObserver(){
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            item.update()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            item.update()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            item.update()
        }

        override fun onChanged() {
            super.onChanged()
            item.update()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)

        }
    }
}
