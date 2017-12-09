package com.cz.recyclerlibrary

import android.util.Log
import android.view.View
import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter
import com.cz.recyclerlibrary.callback.*
import cz.refreshlayout.library.PullToRefreshLayout
import java.util.ArrayList

/**
 * Created by cz on 2017/8/9.
 * 辅助简化所有操作
 */
fun PullToRefreshRecyclerView.onRefresh(listener:()->Unit){
    setOnRefreshListener(object : PullToRefreshLayout.OnPullToRefreshListener {
        override fun onRefresh() {
            listener()
        }
    })
}

/**
 * 列表底部刷新事件
 */
fun PullToRefreshRecyclerView.onFooterRefresh(listener:()->Unit){
    setOnPullFooterToRefreshListener(object :PullToRefreshRecyclerView.OnPullFooterToRefreshListener{
        override fun onRefresh() {
            listener()
        }
    })
}

/**
 * 列表点击事件
 */
fun PullToRefreshRecyclerView.onItemClick(listener:(View, Int,Int)->Unit){
    setOnItemClickListener(OnItemClickListener { v, position, adapterPosition -> listener(v,position,adapterPosition) })
}

/**
 * 列表底部重试事件
 */
fun PullToRefreshRecyclerView.onFootRetry(listener:(View)->Unit){
    setOnFootRetryListener(View.OnClickListener{
        listener(it)
    })
}

/**
 * 列表单选
 */
fun PullToRefreshRecyclerView.onSingleSelect(listener:(View,Int,Int)->Unit){
    setOnSingleSelectListener(object :PullToRefreshRecyclerView.OnSingleSelectListener{
        override fun onSingleSelect(v: View, newPosition: Int, oldPosition: Int) {
            listener(v,newPosition,oldPosition)
        }
    })
}

/**
 * 列表多选事件
 */
fun PullToRefreshRecyclerView.onMultiSelect(listener:(View,ArrayList<Int>, Int,Int)->Unit) {
    setOnMultiSelectListener(object : PullToRefreshRecyclerView.OnMultiSelectListener {
        override fun onMultiSelect(v: View, selectPositions: ArrayList<Int>, lastSelectCount: Int, maxCount: Int) {
            listener(v, selectPositions, lastSelectCount, maxCount)
        }
    })
}

/**
 * 块选选中事件
 */
fun PullToRefreshRecyclerView.onRectangleSelect(listener:(Int,Int)->Unit){
    setOnRectangleSelectListener(object :PullToRefreshRecyclerView.OnRectangleSelectListener{
        override fun onRectangleSelect(startPosition: Int, endPosition: Int) {
            listener(startPosition,endPosition)
        }
    })
}


/**
 * 可展开列表点击
 */
fun PullToRefreshExpandRecyclerView.onExpandItemClick(listener:(View, Int, Int)->Unit){
    setOnExpandItemClickListener(object :OnExpandItemClickListener{
        override fun onItemClick(v: View, groupPosition: Int, childPosition: Int) {
            listener(v,groupPosition,childPosition)
        }
    })
}

/**
 * 拖动列表点击
 */
fun DragRecyclerView.onItemClick(listener:(View, Int,Int)->Unit){
    setOnItemClickListener(OnItemClickListener { v, position, adapterPosition -> listener(v,position,adapterPosition) })
}

/**
 * 拖动拦截监听
 */
fun DragRecyclerView.onDragItemEnable(listener:(Int)->Boolean){
    setOnDragItemEnableListener(object :OnDragItemEnableListener{
        override fun itemEnable(position: Int): Boolean {
            return listener(position)
        }
    })
}

/**
 * 拖动适配器点击
 */
fun DynamicAdapter.onItemClick(listener:(View, Int,Int)->Unit){
    setOnItemClickListener(OnItemClickListener { v, position, adapterPosition -> listener(v,position,adapterPosition) })
}


internal val DEBUG=false
internal inline fun<reified T> T.debugLog(message:String){
    if(DEBUG){
        Log.e("RecyclerView",message)
    }
}