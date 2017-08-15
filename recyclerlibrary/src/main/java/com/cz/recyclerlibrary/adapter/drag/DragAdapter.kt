package com.cz.recyclerlibrary.adapter.drag

import android.support.v7.widget.RecyclerView
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter

/**
 * 可替换元素位置的动态添加数据适配器对象

 * @param
 */
class DragAdapter(adapter:  RecyclerView.Adapter<RecyclerView.ViewHolder>) : DynamicAdapter(adapter) {

    /**
     * 互换元素
     * @param oldPosition
     * *
     * @param newPosition
     */
    fun swap(oldPosition: Int, newPosition: Int) {
        if (oldPosition < newPosition) {
            var i = oldPosition
            while (i < newPosition) {
                swapItem(i, i + 1)
                i++
            }
        } else {
            var i = oldPosition
            while (i > newPosition) {
                swapItem(i, i - 1)
                i--
            }
        }
    }

    /**
     * 转换条目
     * @param oldIndex
     * *
     * @param newIndex
     */
    private fun swapItem(oldIndex: Int, newIndex: Int) {
        val adapter=adapter
        val position1 = findPosition(oldIndex)
        val position2 = findPosition(newIndex)
        val startDynamic = -1 != position1
        val endDynamic = -1 != position2
        //四种置换方式
        if (startDynamic && endDynamic) {
            dynamicHelper[position1].position=newIndex
            dynamicHelper[position2].position=oldIndex
            dynamicHelper.dynamicItems.sortBy { it.position }
        } else if (startDynamic) {
            dynamicHelper[position1].position=newIndex
            dynamicHelper.dynamicItems.sortBy { it.position }
        } else if (endDynamic) {
            dynamicHelper[position2].position=oldIndex
            dynamicHelper.dynamicItems.sortBy { it.position }
        } else if(adapter is BaseViewAdapter<*>){
            adapter.swapItem(oldIndex - getStartPosition(oldIndex), newIndex - getStartPosition(newIndex))
        }
    }


}