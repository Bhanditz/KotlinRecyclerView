package com.ldzs.recyclerlibrary.adapter.drag

import android.view.View

import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter

/**
 * 可替换元素位置的动态添加数据适配器对象

 * @param
 */
class DragAdapter(adapter: BaseViewAdapter<*>) : DynamicAdapter(adapter) {

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
            dysDy(oldIndex, newIndex)
        } else if (startDynamic) {
            dysItem(oldIndex, newIndex, position1)
        } else if (endDynamic) {
            itemsDy(oldIndex, newIndex)
        } else if(adapter is BaseViewAdapter<*>){
            adapter.swapItem(oldIndex - getStartIndex(oldIndex), newIndex - getStartIndex(newIndex))
        }
    }

    private fun itemsDy(oldIndex: Int, newIndex: Int) {
        val position = findPosition(newIndex)
        val newViewType = fullItemTypes.get(newIndex)
        val index = fullItemTypes.indexOfKey(newIndex)
        fullItemTypes.removeAt(index)
        fullItemTypes.put(oldIndex, newViewType)
        itemPositions[position] = oldIndex//重置角标位置
    }

    /**
     * 动态条目置换普通条目

     * @param oldPosition
     * *
     * @param newPosition
     * *
     * @param position1
     */
    private fun dysItem(oldPosition: Int, newPosition: Int, position1: Int) {
        //直接更换插入对象到指定位置,装饰对象不用改动
        val newViewType = fullItemTypes.get(oldPosition)
        val index = fullItemTypes.indexOfKey(oldPosition)
        fullItemTypes.removeAt(index)
        fullItemTypes.put(newPosition, newViewType)
        itemPositions[position1] = newPosition//重置角标位置
    }

    /**
     * 动态条目置换动态条目

     * @param oldPosition
     * *
     * @param newPosition
     */
    private fun dysDy(oldPosition: Int, newPosition: Int) {
        val oldViewType = fullItemTypes.get(oldPosition)
        val newViewType = fullItemTypes.get(newPosition)
        fullItemTypes.put(oldPosition, newViewType)
        fullItemTypes.put(newPosition, oldViewType)
        //替换view
        val oldView = fullViews.get(oldViewType)
        val newView = fullViews.get(newViewType)
        fullViews.put(oldViewType, newView)
        fullViews.put(newViewType, oldView)
    }

}