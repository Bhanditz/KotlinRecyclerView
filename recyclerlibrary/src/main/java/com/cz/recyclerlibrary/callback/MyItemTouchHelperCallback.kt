package com.cz.recyclerlibrary.callback

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

import com.cz.recyclerlibrary.adapter.drag.DragAdapter

/**
 * Created by Alessandro on 12/01/2016.
 * 动态添加头尾Adapter的数据刷新通知对象
 */
class MyItemTouchHelperCallback(private val callbackItemTouch: CallbackItemTouch) : ItemTouchHelper.Callback() {
    private var dragListener: OnDragItemEnableListener? = null
    private var longPressDragEnable: Boolean = false//长按是否启用拖动
    private var dynamicViewDragEnable: Boolean = false//动态添加view是否启用拖动
    var adapter: DragAdapter? = null

    /**
     * 设置长按是否拖动

     * @param enable
     */
    fun setLongPressDrawEnable(enable: Boolean) {
        this.longPressDragEnable = enable
    }

    override fun isLongPressDragEnabled(): Boolean {
        return longPressDragEnable
    }


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val adapter=adapter
        val dragListener=dragListener
        val position = viewHolder.adapterPosition
        var flag = ItemTouchHelper.Callback.makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        if (null != adapter) {
            val index = adapter.findPosition(position)
            //动态添加的并启用的,可以拖动.或者自身条目本身启用可以拖动的.
            if (RecyclerView.NO_POSITION != index && dynamicViewDragEnable || null != dragListener && !dragListener.itemEnable(position - adapter.getStartPosition(position))) {
                flag = ItemTouchHelper.Callback.makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.DOWN)
            }
        }
        return flag
    }

    /**
     * 设置拖动条目启用监听

     * @param listener
     */
    fun setDragItemEnableListener(listener: OnDragItemEnableListener) {
        this.dragListener = listener
    }

    /**
     * 设置动态添加view是否启用拖动
     * @param enable
     */
    fun setDynamicViewDragEnable(enable: Boolean) {
        this.dynamicViewDragEnable = enable
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val adapter=adapter
        val dragListener=dragListener
        val position = viewHolder.adapterPosition
        val targetPosition = target.adapterPosition
        var itemEnable = false
        if (null != adapter) {
            val index = adapter.findPosition(targetPosition)
            if (RecyclerView.NO_POSITION != index) {
                itemEnable = dynamicViewDragEnable
            } else if (null != dragListener && dragListener.itemEnable(targetPosition - adapter.getStartPosition(position))) {
                itemEnable = true
            }
        } else {
            itemEnable = null != dragListener && dragListener.itemEnable(targetPosition)
        }
        if (itemEnable) {
            callbackItemTouch.onItemMove(position, targetPosition)
        }
        return itemEnable
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

}
