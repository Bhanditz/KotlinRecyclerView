package com.cz.recyclerlibrary.adapter.drag

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.HeaderAdapter
import com.cz.recyclerlibrary.callback.GridSpanCallback
import com.cz.recyclerlibrary.callback.OnItemClickListener
import com.cz.recyclerlibrary.callback.OnItemLongClickListener

import java.util.ArrayList
import java.util.Arrays

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
 * --欢迎来到信用钱包--
 * item3

 * Model item==2
 *
 *
 * 难点在于,如果将随机位置添加的自定义view的位置动态计算,不影响被包装Adapter

 * @param
 */
open class DynamicAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) : HeaderAdapter(adapter) {
    protected val START_POSITION = 1 shl 6//超出其他Header/Footer范围,避免混乱
    protected val fullItemTypes: SparseIntArray
    protected val fullViews: SparseArray<View>
    var headerViewCount: Int = 0
    protected var itemPositions: IntArray
    private var itemViewCount: Int = 0
    private var longItemListener: OnItemLongClickListener? = null
    private var itemClickListener: OnItemClickListener? = null


    init {
        itemPositions = IntArray(0)
        fullItemTypes = SparseIntArray()
        fullViews = SparseArray<View>()
    }

    /**
     * 条目范围插入

     * @param positionStart
     * *
     * @param itemCount
     */
    fun itemRangeInsert(positionStart: Int, itemCount: Int) {
        //重置所有移除范围内的动态条信息
        val itemPositionLists = ArrayList<Int>()
        val newFullItems = SparseIntArray()
        val length = itemPositions.size
        for (i in 0..length - 1) {
            val position = itemPositions[i]
            var newPosition = position
            //范围外条目,整体后退
            if (positionStart <= position) {
                newPosition = position + itemCount
            }
            newFullItems.put(newPosition, fullItemTypes.get(position))
            itemPositionLists.add(newPosition)
        }
        fullItemTypes.clear()
        for (i in 0..newFullItems.size() - 1) {
            fullItemTypes.append(newFullItems.keyAt(i), newFullItems.valueAt(i))
        }
        val size = itemPositionLists.size
        itemPositions = IntArray(size)
        for (i in 0..size - 1) {
            itemPositions[i] = itemPositionLists[i]
        }
        notifyItemRangeInserted(positionStart, itemCount)
    }

    /**
     * 全局范围内条目删除
     * 范围内删除所有条目,包括自定义添加条目
     * @param positionStart
     * *
     * @param removeCount
     */
    fun itemRangeGlobalRemoved(positionStart: Int, removeCount: Int) {
        var positionStart = positionStart
        var removeCount = removeCount
        //重置所有移除范围内的动态条信息
        val startIndex = getStartIndex(positionStart)
        positionStart += startIndex
        //计算出最后移除范围
        var index = 0
        var positionEnd = positionStart
        while (index < removeCount) {
            if (!isDynamicItem(positionEnd++)) index++
        }
        removeCount = positionEnd - positionStart

        val positionList = ArrayList<Int>()
        run {
            var i = 0
            while (i < itemPositions.size) {
                positionList.add(itemPositions[i++])
            }
        }

        for (position in positionStart..positionEnd - 1) {
            if (isDynamicItem(position)) {
                val value = fullItemTypes.valueAt(startIndex)
                fullViews.remove(value)
                fullItemTypes.removeAt(startIndex)
                positionList.removeAt(startIndex)
            }
        }
        //        Log.e(TAG,"array:"+positionList);
        val size = fullItemTypes.size()
        for (i in startIndex..size - 1) {
            val position = positionList[i]
            val newPosition = position - removeCount
            val value = fullItemTypes.get(position)
            fullItemTypes.delete(position)
            fullItemTypes.put(newPosition, value)
            positionList[i] = newPosition
        }
        itemPositions = IntArray(size)
        var i = 0
        while (i < size) {
            itemPositions[i] = positionList[i]
            i++
        }
        if (0 < removeCount) {
            notifyItemRangeRemoved(positionStart, removeCount)
        }
        //        Log.e(TAG,"position:"+Arrays.toString(itemPositions)+" positionStart:"+positionStart+" positionEnd:"+positionEnd+" startIndex:"+startIndex+" realCount:"+getRealItemCount()+" itemCount:"+removeCount);
    }

    /**
     * 条目范围内删除,用户条目,不包含自定义插入条目
     * like remove 0 from 8
     * --0--
     * 1 2 3
     * --4--
     * 5 6
     * --7--
     * 8 9 10
     * 11 12 13
     * result:
     * --0--
     * (1 2 3)
     * --4--
     * (5 6)
     * --7--
     * (8 9 10)
     * 11 12 13

     * 难度最大的地方在于.动态移除.以及动态插件条目信息更新
     * 1:先计算出,当前移除位置,到指定需要移除位置条目数的最终位置.上面示例是从从0开始,移除8个,那么最终位置为11
     * 2:范围移除.但是中间有自定义条目插入.所以其中移除还是分段移除.并且更新信息.这里需要算一步,更新信息,再删一步.
     * 如(1,2,3)这一段.起始位置为1(--0--),需要删除3个,会记录删除偏移量3,然后检测到(--4--),动态更新(--4--)条目信息.
     * 将其往前移3,删除1-3元素后,(--4--)的起始变为1,后续逻辑相同.
     * 3:任何范围外的超出的,都会直接减去最终的startOffset值,形成插入信息一致更新.

     * 为实现此效果.中间修改代码很多次.主要是没想通具体逻辑.就是第二步的逻辑.只有达到此效果.才是真正的动态化.
     * @param positionStart
     * *
     * @param itemCount
     */
    fun itemRangeRemoved(positionStart: Int, itemCount: Int) {
        //重置所有移除范围内的动态条信息
        var startIndex = getStartIndex(positionStart)
        //计算出最后移除范围
        var index = 0
        var positionEnd = positionStart
        while (index < itemCount) {
            if (!isDynamicItem(positionEnd++)) index++
        }
        //        Log.e(TAG,"itemCount:"+itemCount+" adapterCount:"+adapter.getItemCount()+" start:"+positionStart+" end:"+positionEnd);

        val length = itemPositions.size
        val finalArray = IntArray(length)
        System.arraycopy(itemPositions, 0, finalArray, 0, length)

        var start = 0
        var startOffset = 0
        var totalOffset = 0
        for (position in positionStart..positionEnd - 1) {
            val isDefaultItem = RecyclerView.NO_POSITION == findPosition(finalArray, position)
            if (isDefaultItem) {
                if (0 == startOffset) start = position - totalOffset
                startOffset++
                totalOffset++
            }
            //判断为插入条目,或者最后一个时,执行偏移运算
            if (!isDefaultItem || positionEnd - 1 == position) {
                for (i in startIndex..length - 1) {
                    val itemPosition = itemPositions[i]
                    itemPositions[i] -= startOffset
                    val value = fullItemTypes.get(itemPosition)
                    fullItemTypes.delete(itemPosition)
                    fullItemTypes.put(itemPositions[i], value)
                }
                //                Log.e(TAG,"start:"+start+" offset:"+startOffset+" index:"+startIndex);
                notifyItemRangeRemoved(start, startOffset)
                startIndex++
                startOffset = 0
            }
        }
    }


    /**
     * 添加一个自定义view到末尾

     * @param layout
     */
    fun addDynamicView(context: Context, @LayoutRes layout: Int) {
        val view = View.inflate(context, layout, null)
        addDynamicView(view, realItemCount)
    }

    /**
     * 添加一个自定义view到指定位置

     * @param position
     */
    fun addDynamicView(view: View?, position: Int) {
        if (RecyclerView.NO_POSITION != findPosition(position)) return //己存在添加位置,则不添加
        val length = itemPositions.size
        val newPositions = IntArray(length + 1)
        newPositions[length] = position
        System.arraycopy(itemPositions, 0, newPositions, 0, itemPositions.size)
        Arrays.sort(newPositions)
        itemPositions = newPositions
        val viewType = START_POSITION + itemViewCount++
        fullItemTypes.put(position, viewType)
        fullViews.put(viewType, view)

        updateHeaderViewCount()
        //当只有一个时,通知插入,这里有一个问题,暂时未找到原因:如果谁清楚,请帮助解决一下,所以不用notifyItemInserted改用notifyDataSetChanged,性能差一点,但不会报错.
        // java.lang.IllegalArgumentException: Called removeDetachedView withBinary a view which is not flagged as tmp detached.ViewHolder{3c6be8ee position=17 id=-1, oldPos=-1, pLpos:-1}
        notifyItemInserted(position)
    }

    /**
     * update header view count
     */
    private fun updateHeaderViewCount() {
        val length = itemPositions.size
        headerViewCount = 0
        var index = 0
        while (index < length && index == itemPositions[index]) {
            index++
            headerViewCount++
        }
    }

    /**
     * 由子类复写.返回装饰底部控件个数
     * @return
     */
    open val footerViewCount: Int
        get() = 0

    /**
     * 移除指定view

     * @param view
     */
    fun removeDynamicView(view: View?) {
        var index = fullViews.indexOfValue(view)
        if (-1 < index) {
            val viewType = fullViews.keyAt(index)
            index = fullItemTypes.indexOfValue(viewType)
            if (-1 < index) {
                val position = fullItemTypes.keyAt(index)
                removeDynamicView(position)
            }
        }
    }

    fun indexOfDynamicView(view: View): Int {
        return fullViews.indexOfValue(view)
    }


    /**
     * 移除指定位置view

     * @param removePosition
     */
    fun removeDynamicView(removePosition: Int) {
        if (isDynamicItem(removePosition)) {
            val itemType = getItemViewType(removePosition)
            fullViews.delete(itemType)
            val length = itemPositions.size
            val newPositions = IntArray(length - 1)
            val newFullItems = SparseIntArray()
            run {
                var i = 0
                var k = 0
                while (i < length) {
                    val position = itemPositions[i]
                    if (removePosition != position) {
                        var newPosition = position
                        if (removePosition < position) {
                            newPosition = position - 1
                        }
                        newPositions[k++] = newPosition
                        newFullItems.put(newPosition, fullItemTypes.get(position))
                    }
                    i++
                }
            }
            fullItemTypes.clear()
            for (i in 0..newFullItems.size() - 1) {
                fullItemTypes.append(newFullItems.keyAt(i), newFullItems.valueAt(i))
            }
            itemPositions = newPositions
            updateHeaderViewCount()
            notifyItemRemoved(removePosition)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView!!.layoutManager
        if (manager is GridLayoutManager) {
            val gridLayoutManager = manager
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    var spanCount = 1
                    if (null != adapter && adapter is GridSpanCallback) {
                        spanCount = (adapter as GridSpanCallback).getSpanSize(gridLayoutManager, position - headerViewCount)
                    }
                    return if (isDynamicItem(position) || isFullItem(position)) gridLayoutManager.spanCount else spanCount
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        val position = holder!!.layoutPosition
        val layoutParams = holder.itemView.layoutParams
        if (null != layoutParams && layoutParams is StaggeredGridLayoutManager.LayoutParams && (isDynamicItem(position) || isFullItem(position))) {
            layoutParams.isFullSpan = true
        }
    }

    /**
     * 由子类实现,条目是否铺满
     * @see .onViewAttachedToWindow .onAttachedToRecyclerView

     * @param position
     * *
     * @return
     */
    protected open fun isFullItem(position: Int): Boolean {
        return false
    }

    /**
     * 判断当前显示是否为自定义铺满条目

     * @param position
     * *
     * @return
     */
    private fun isDynamicItem(position: Int): Boolean {
        return RecyclerView.NO_POSITION != findPosition(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var holder: RecyclerView.ViewHolder? = null
        val view = fullViews.get(viewType)
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
        if (null != adapter&&!isDynamicItem(position)) {
            holder.itemView.setOnClickListener { v ->
                //这里看起来很矛盾,其实是必然的设计,因为position可以往下减为真实的子Adapter的位置,但是往上,无法逆反,为实现drag条目转换功能,所以只能传递真实位置回具体条目
                val itemPosition = holder.adapterPosition
                val realPosition = itemPosition - getStartIndex(itemPosition)
                if (onItemClick(v, realPosition) && null != itemClickListener) {
                    itemClickListener?.onItemClick(v, itemPosition)
                }
            }
            val startIndex = getStartIndex(position)
            adapter.onBindViewHolder(holder, position - startIndex)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        val index = findPosition(position)
        if (RecyclerView.NO_POSITION != index) {
            viewType = fullItemTypes.get(position)
        } else if (null != adapter) {
            val startIndex = getStartIndex(position)
            viewType = adapter!!.getItemViewType(position - startIndex)
        }
        return viewType
    }

    fun findDynamicView(@IdRes id: Int): View? {
        var findView: View? = null
        for (i in 0..fullViews.size() - 1) {
            val view = fullViews.valueAt(i)
            findView = view.findViewById(id)
            if (null != findView) {
                break
            }
        }
        return findView
    }

    private val realItemCount: Int
        get() = itemCount - footerViewCount

    override fun getItemCount(): Int {
        var itemCount = fullViews.size()
        if (null != adapter) {
            itemCount += adapter!!.itemCount
        }
        //        Log.e(TAG,"Dynamic itemCount:"+itemCount+" fullView:"+fullViews.size());
        return itemCount
    }


    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置

     * @return
     */
    fun getStartIndex(position: Int): Int {
        val positions = itemPositions
        var start = 0
        var end = positions.size - 1
        var result = -1
        while (start <= end) {
            val middle = (start + end) / 2
            if (position == positions[middle]) {
                result = middle + 1
                break
            } else if (position < positions[middle]) {
                end = middle - 1
            } else {
                start = middle + 1
            }
        }
        if (-1 == result) {
            result = start
        } else {
            start = result - 1
            end = positions.size - 1
            //当position为0时,插入条目为0,1 这时候应该取得2
            while (start < end && positions[start] + 1 == positions[start + 1]) {
                start++
                result++
            }
        }
        return result
    }

    fun findPosition(position: Int): Int {
        return findPosition(itemPositions, position)
    }

    /**
     * 查找当前是否有返回值
     * @param array
     * *
     * @param position
     * *
     * @return
     */
    fun findPosition(array: IntArray, position: Int): Int {
        val positions = array
        var start = 0
        var end = positions.size - 1
        var result = -1
        while (start <= end) {
            val middle = (start + end) / 2
            if (position == positions[middle]) {
                result = middle
                break
            } else if (position < positions[middle]) {
                end = middle - 1
            } else {
                start = middle + 1
            }
        }
        return result
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * 获得添加view个数

     * @return
     */
    val dynamicItemCount: Int
        get() = fullViews.size()

    /**
     * 设置条目长按点击事件

     * @param listener
     */
    fun setOnLongItemClickListener(listener: OnItemLongClickListener) {
        this.longItemListener = listener
    }

}