package com.cz.recyclerlibrary

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import com.cz.recyclerlibrary.adapter.expand.ExpandAdapter
import com.cz.recyclerlibrary.callback.OnExpandItemClickListener


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

    override var adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?
        get()=super.adapter
        set(adapter) {
            if (adapter !is ExpandAdapter<*, *>) {
                throw IllegalArgumentException("Adapter must extend ExpandAdapter!")
            } else {
                super.adapter=adapter
                expandAdapter = adapter
                expandAdapter?.setHeaderViewCount(headerViewCount)
                expandAdapter?.setOnExpandItemClickListener(object :OnExpandItemClickListener{
                    override fun onItemClick(v: View, groupPosition: Int, childPosition: Int) {
                        expandItemClickListener?.onItemClick(v, groupPosition, childPosition)
                    }
                })
            }
        }

    override fun addHeaderView(view: View?) {
        super.addHeaderView(view)
        expandAdapter?.setHeaderViewCount(headerViewCount)
    }

    override fun addDynamicView(view: View?, position: Int) =throw UnsupportedOperationException("Required method addDynamicView can't used")

    fun setOnExpandItemClickListener(listener: OnExpandItemClickListener) {
        this.expandItemClickListener = listener
    }

}

