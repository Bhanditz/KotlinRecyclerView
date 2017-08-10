package com.cz.sample.adapter

import android.content.Context
import android.support.annotation.ArrayRes
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.CacheViewHolder

import java.util.Arrays

/**
 * Created by cz on 16/1/23.
 */
class SimpleAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : BaseViewAdapter<E>(context, items) {
    companion object {
        private val TAG = "SimpleAdapter"
        fun createFromResource(context: Context, @ArrayRes res: Int): SimpleAdapter<*> {
            return SimpleAdapter(context, context.resources.getStringArray(res))
        }
    }

    constructor(context: Context, items: Array<E>) : this(context, R.layout.simple_text_item, Arrays.asList(*items)) {}

    constructor(context: Context, @LayoutRes layout: Int, items: Array<E>) : this(context, layout, Arrays.asList(*items)) {}

    constructor(context: Context, items: List<E>) : this(context, R.layout.simple_text_item, items) {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Log.e(TAG, "onCreateViewHolder")
        return CacheViewHolder(inflateView(parent, layout))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder:" + position)
        val textView = holder.itemView as TextView
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
    }

}
