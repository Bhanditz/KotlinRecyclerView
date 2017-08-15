package com.cz.recyclerlibrary

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.drag.DragAdapter
import com.cz.recyclerlibrary.callback.CallbackItemTouch
import com.cz.recyclerlibrary.callback.MyItemTouchHelperCallback
import com.cz.recyclerlibrary.callback.OnDragItemEnableListener
import com.cz.recyclerlibrary.callback.OnItemClickListener
import com.cz.recyclerlibrary.observe.DynamicAdapterDataObserve

/**
 * 可拖动排序的gridView

 * @date 2015/8/23
 * *
 *
 *
 */
class DragRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle), CallbackItemTouch {
    private val helperCallback: MyItemTouchHelperCallback = MyItemTouchHelperCallback(this)
    private lateinit var dragAdapter: DragAdapter

    init {
        helperCallback.setLongPressDrawEnable(true)
    }

    /**
     * 获得子条目的位置

     * @param position
     * *
     * @return
     */
    fun getItemPosition(position: Int): Int {
        return position - dragAdapter.getStartPosition(position)
    }

    /**
     * 只有继承BaseViewAdapter才会

     * @param adapter
     */
    override fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        if (adapter is BaseViewAdapter<*>) {
            dragAdapter = DragAdapter(adapter)
            super.setAdapter(dragAdapter)
            adapter.registerAdapterDataObserver(DynamicAdapterDataObserve(dragAdapter))
            helperCallback.adapter=dragAdapter
            ItemTouchHelper(helperCallback).attachToRecyclerView(this)
        } else {
            throw IllegalArgumentException("adapter must be extends BaseViewAdapter!")
        }
    }

    /**
     * 设置拖动条目启用监听

     * @param listener
     */
    fun setOnDragItemEnableListener(listener: OnDragItemEnableListener) {
        helperCallback.setDragItemEnableListener(listener)
    }

    /**
     * 设置长按是否拖动

     * @param enable
     */
    fun setLongPressDrawEnable(enable: Boolean) {
        helperCallback.setLongPressDrawEnable(enable)
    }

    /**
     * 设置条目移动.

     * @param oldPosition
     * *
     * @param newPosition
     */
    fun setItemMove(oldPosition: Int, newPosition: Int) {
        dragAdapter.swap(oldPosition, newPosition)
        dragAdapter.notifyItemMoved(oldPosition, newPosition)
        //动态结束后,刷新条目
        postDelayed({ dragAdapter.notifyItemChanged(newPosition) }, itemAnimator.moveDuration)
    }

    override fun onItemMove(oldPosition: Int, newPosition: Int) {
        setItemMove(oldPosition, newPosition)
    }

    /**
     * 动态添加view

     * @param layout
     */
    fun addDynamicView(@LayoutRes layout: Int, position: Int) {
        dragAdapter.addDynamicView(LayoutInflater.from(context).inflate(layout, this, false), position)
    }

    /**
     * 动态添加view

     * @param view
     */
    fun addDynamicView(view: View, position: Int) {
        dragAdapter.addDynamicView(view, position)
    }

    /**
     * 设置条目点击

     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        dragAdapter.setOnItemClickListener(listener)
    }


}