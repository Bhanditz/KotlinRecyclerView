package com.cz.sample.ui.layoutmanager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.cz.recyclerlibrary.adapter.BaseViewHolder

import com.cz.sample.R

import java.util.Arrays


/**
 * Created by Administrator on 2017/1/15.
 */

class SampleAdapter(context: Context, private val items: List<String>) : RecyclerView.Adapter<BaseViewHolder>() {
    companion object {
        val TAG = "SimpleAdapter"
    }
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    constructor(context: Context, items: Array<String>) : this(context, Arrays.asList(*items))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Log.e(TAG, "onCreateViewHolder")
        var viewHolder: RecyclerView.ViewHolder
        if (0 == viewType) {
            viewHolder = BaseViewHolder(layoutInflater.inflate(R.layout.layout1_item, parent, false))
        } else {
            viewHolder = BaseViewHolder(layoutInflater.inflate(R.layout.layout2_item, parent, false))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder:" + position)
        val itemViewType = getItemViewType(position)
        val item = this.items[position]
        if (0 == itemViewType) {
            val textView1 = holder.itemView.findViewById(R.id.text1) as TextView
            val textView2 = holder.itemView.findViewById(R.id.text2) as TextView
            textView1.text = item + "1"
            textView2.text = item + "2"
        } else if (1 == itemViewType) {
            val textView1 = holder.itemView.findViewById(R.id.text1) as TextView
            textView1.text = item
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        if (0 != position % 2) {
            viewType = 1
        }
        return viewType
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
