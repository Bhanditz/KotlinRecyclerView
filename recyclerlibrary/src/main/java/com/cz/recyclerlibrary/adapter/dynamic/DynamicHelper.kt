package com.cz.recyclerlibrary.adapter.dynamic

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cz.recyclerlibrary.debugLog
import java.util.*

/**
 * Created by cz on 2017/8/15.
 */
class DynamicHelper(val adapter:RecyclerView.Adapter<RecyclerView.ViewHolder>){
    companion object {
        val TYPE_START = -1//从-1起始开始减
        val TYPE_END = 1 shl 8+1//超出其他Header/Footer范围,避免混乱
    }
    val headerItems= mutableListOf<DynamicView>()//刷新头
    val footerItems= mutableListOf<DynamicView>()//刷新尾
    val dynamicItems= mutableListOf<DynamicView>()//动态条目
    var headerTotalCount=0
    var endTotalCount =0

    //底部起始位置
    val footerStartIndex:Int
        get() = adapter.itemCount-footerViewCount

    /**
     * 获得添加头个数
     * @return
     */
    val headerViewCount: Int
        get() = headerItems.size

    /**
     * 获得添加底部控件个数
     * @return
     */
    val footerViewCount: Int
        get() = footerItems.size

    /**
     * 获得添加动态view个数
     * @return
     */
    val dynamicItemCount: Int
        get() = dynamicItems.size


    val itemCount:Int
        get() = headerViewCount+footerViewCount+dynamicItemCount

    fun addHeaderView(view: View?)=addHeaderView(view,headerItems.size)

    fun addHeaderView(view: View?,index:Int=0) {
        val view=view?:return
        val viewType = TYPE_START - headerTotalCount++
        this.headerItems.add(index, DynamicView(view,viewType))
        adapter.notifyItemInserted(index)
    }

    /**
     * 添加一个底部控件
     * @param view
     * *
     */
    fun addFooterView(view: View) =addFooterView(view,footerViewCount)

    /**
     * 此方法不开放,避免角标混乱
     * @param view
     * *
     * @param index
     */
    fun addFooterView(view: View,index: Int) {
        val viewType = TYPE_END + endTotalCount++
        footerItems.add(index,DynamicView(view,viewType))//越界处理
        adapter.notifyItemInserted(footerStartIndex+index)
    }

    /**
     * 获得指定位置的headerView
     * @param index
     * *
     * @return
     */
    fun getHeaderView(index: Int): View? {
        var view: View? = null
        if (index in 0..headerViewCount-1) {
            view = headerItems[index].view
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
        if (index in 0..footerViewCount-1) {
            view = footerItems[index].view
        }
        return view
    }

    /**
     * 移除指定的HeaderView对象
     * @param view
     */
    fun removeHeaderView(view: View?) =removeHeaderView(headerItems.indexOfFirst { it.view == view })

    /**
     * 移除指定的HeaderView对象
     * @param position
     */
    fun removeHeaderView(position: Int) {
        if(position in 0..headerViewCount-1){
            headerItems.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

    /**
     * 移除指定的HeaderView对象
     * @param view
     */
    fun removeFooterView(view: View?) =removeFooterView(footerItems.indexOfFirst { it.view==view })

    /**
     * 移除指定的HeaderView对象
     * @param position
     */
    fun removeFooterView(position: Int) {
        if (position in 0..footerViewCount-1) {
            footerItems.removeAt(position)
            adapter.notifyItemRemoved(footerStartIndex + position)
        }
    }

    fun findDynamicView(@IdRes id: Int): View?{
        var findView: View? = null
        headerItems.find {
            findView=it.view.findViewById(id)
            null!=findView
        }
        if (null == findView) {
            footerItems.find {
                findView=it.view.findViewById(id)
                null!=findView
            }
        }
        if (null == findView) {
            dynamicItems.find {
                findView=it.view.findViewById(id)
                null!=findView
            }
        }
        return findView
    }

    fun isWrapperPosition(position: Int)=isDynamicPosition(position)||isHeaderPosition(position)||isFooterPosition(position)

    fun isHeaderPosition(position: Int)=position in 0..headerViewCount-1

    fun isFooterPosition(position: Int)=position in footerStartIndex..adapter.itemCount-1

    fun getDynamicView(viewType:Int):View?{
        var findView: View? = headerItems.find { it.viewType==viewType }?.view
        if (null == findView) {
            findView = footerItems.find { it.viewType==viewType }?.view
        }
        if (null == findView) {
            findView = dynamicItems.find { it.viewType==viewType }?.view
        }
        return findView
    }

    fun getItemViewType(position: Int):Int{
        var viewType:Int=0
        if(isHeaderPosition(position)){
            viewType=headerItems[position].viewType
        } else if(isFooterPosition(position)){
            viewType=footerItems[position-footerStartIndex].viewType
        } else if(isDynamicPosition(position)){
            val position = findPosition(position)
            viewType=dynamicItems[position].viewType
        }
        return viewType
    }


    val itemPositions:IntArray
        get() =dynamicItems.map { it.position }.toIntArray()
    /**
     * 条目范围插入
     * @param positionStart 此处传入的为被包装的position数据,所以需要动态加入头,与起始
     * @param itemCount
     */
    fun itemRangeInsert(positionStart: Int, itemCount: Int) {
        debugLog("itemRangeInsert:${dynamicItems.map { it.position }}")
        //这里获取从0到插件元素范围内,元素总数,因为没人知道,动态条目在此范围内,到抵插入了多少个,到positionStart
        val position= headerViewCount+getAdapterPosition(positionStart)
        debugLog("itemRangeInsert:$positionStart acrossItemCount:$position itemCount:$itemCount")
        forEach {
            //重置所有移除范围内的动态条信息
            if (position < it.position) {
                it.position += itemCount//范围外条目,整体后退
            }
        }
        debugLog("itemRangeInsert:${dynamicItems.map { it.position }}")
        //如果当前插入位置为一个动态条目位置,向前+1
        if(isDynamicPosition(position))
            adapter.notifyItemRangeInserted(position+1, itemCount)
        else adapter.notifyItemRangeInserted(position, itemCount)
    }

    /**
     * 全局范围内条目删除
     * 范围内删除所有条目,包括自定义添加条目
     * @param positionStart
     * @param itemCount
     */
    fun itemRangeRemoved(positionStart: Int, itemCount: Int) {
        //1:计算移除总范围,以一个不断增加的数值,排除非动态条目的总个数条目
        val positionStart= headerViewCount+getAdapterPosition(positionStart)
        //复原原数据列大小,因为外围传入到这里时,数据已经移除了,所以添加回去
        val adapterCount=adapter.itemCount+itemCount-footerViewCount
        //获取可移除范围尾,并与操作前数据列比较,否则可能会过界
        var positionEnd = Math.min(adapterCount, getAdapterPosition(positionStart+itemCount))
        val acrossItemCount=positionEnd-positionStart
        //可移除数据范围
        val removeRange=positionStart..positionEnd-1

        //2:移除范围内条目
        dynamicItems.removeAll { it.position in removeRange }
        //3:前条目往前移
        forEach {
            if(it.position > removeRange.last){
                it.position-=acrossItemCount
            }
        }
        //移除所有范围内条目
        adapter.notifyItemRangeRemoved(positionStart, acrossItemCount)
    }

    /**
     * 获得一个一个子Adapter 指定Position的经过所有条目个数
     */
    private fun getAdapterPosition(position: Int): Int {
        var itemCount = 0
        var totalCount = position
        val startPosition = getStartPosition(position)
        while(itemCount<startPosition){
            //越界跳出
            if (!isDynamicPosition(totalCount)) {
                //添加移除条目个数,移除条目非动态条目<position,则一直循环,直到到达指定位置为止,效率不高
                itemCount++
            }
            totalCount++
        }
        return totalCount
    }


    /**
     * 添加一个自定义view到指定位置
     * @param position
     */
    fun addDynamicView(view: View?, position: Int) {
        val view=view?:return
        forEach {
            //>= 条目向后移
            if(it.position>=position){
                it.position++
            }
        }
        //计算dynamic position时,不包含header count
        val viewType = DynamicHelper.TYPE_END + endTotalCount++
        dynamicItems.add(DynamicHelper.DynamicView(view,viewType,position))
        dynamicItems.sortBy { it.position }
        debugLog("addDynamicView:${dynamicItems.map { it.position }} $dynamicItemCount")
        adapter.notifyItemInserted(position)
    }



    /**
     * 移除指定view

     * @param view
     */
    fun removeDynamicView(view: View?) =removeDynamicView(dynamicItems.indexOfFirst { it.view==view })

    /**
     * 判断当前显示是否为自定义铺满条目
     * @param position
     * *
     * @return
     */
    fun isDynamicPosition(position: Int): Boolean =RecyclerView.NO_POSITION != findPosition(position)

    /**
     * 移除指定位置动态view
     * @param position
     */
    fun removeDynamicView(position: Int) {
        if (position in 0..dynamicItemCount-1) {
            val dynamicView=dynamicItems[position]
            forEach {
                //越界条目往前移
                if(it.position>dynamicView.position){
                    it.position--
                }
            }
            //移除当前条目
            dynamicItems-=dynamicView
            //通知删除当前位置
            adapter.notifyItemRemoved(dynamicView.position)
        }
    }


    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置

     * @return
     */
    fun getStartPosition(position: Int): Int {
        val positions = itemPositions
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
        if (-1 == result) {
            result = start
        }
        return result
    }

    /**
     * 查找内置映射元素位置,此处不能使用Arrays.binarySearch
     */
    fun findPosition(position: Int): Int = findPosition(itemPositions,position)

    /**
     * 查找当前是否有返回值
     * @param array
     * @param position
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

    class DynamicView(val view:View,val viewType:Int,var position:Int=-1)

    operator fun get(position: Int)=dynamicItems[position]

    operator fun set(position: Int,item:DynamicView){
        dynamicItems[position]=item
    }

    fun forEach(action:(DynamicView)->Unit)=dynamicItems.forEach(action)

}