package com.ldzs.recyclerlibrary

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import com.ldzs.recyclerlibrary.adapter.expand.ExpandAdapter
import com.ldzs.recyclerlibrary.callback.OnExpandItemClickListener
import com.ldzs.recyclerlibrary.callback.OnItemClickListener


/**
 * Created by cz on 16/1/22.
 * 可展开的RecyclerView对象
 */
class PullToRefreshExpandRecyclerView : PullToRefreshRecyclerView {
    private var expandItemClickListener: OnExpandItemClickListener? = null
    private var expandAdapter: ExpandAdapter<*, *>?=null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        if (adapter !is ExpandAdapter<*, *>) {
            throw IllegalArgumentException("Adapter must extend ExpandAdapter!")
        } else {
            super.setAdapter(adapter)
            expandAdapter = adapter
            expandAdapter?.setHeaderViewCount(headerViewCount)
            expandAdapter?.setOnExpandItemClickListener(object :OnExpandItemClickListener{
                override fun onItemClick(v: View, groupPosition: Int, childPosition: Int) {
                    expandItemClickListener?.onItemClick(v, groupPosition, childPosition)
                }
            })
        }
    }

    override fun addHeaderView(view: View) {
        super.addHeaderView(view)
        expandAdapter?.setHeaderViewCount(headerViewCount)
    }


    fun setOnExpandItemClickListener(listener: OnExpandItemClickListener) {
        this.expandItemClickListener = listener
    }

}

