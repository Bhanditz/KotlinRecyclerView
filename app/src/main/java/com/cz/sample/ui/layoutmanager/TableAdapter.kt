package com.cz.sample.ui.layoutmanager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView

import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.sample.R


/**
 * Created by cz on 2017/1/21.
 */

class TableAdapter(context: Context, items: List<String>) : BaseViewAdapter<String>(context, items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflateView(parent, R.layout.table_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        val viewGroup = holder.itemView as ViewGroup
        val childCount = viewGroup.childCount
        for (i in 0..childCount - 1) {
            val textView = viewGroup.getChildAt(i) as TextView
            textView.text = item + " column:" + i
        }
    }
}
