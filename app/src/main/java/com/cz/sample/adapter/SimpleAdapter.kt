package com.cz.sample.adapter

import android.content.Context
import android.graphics.Color
import android.support.annotation.ArrayRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.DividerInterceptCallback
import com.cz.recyclerlibrary.layoutmanager.base.ViewScrollOffsetCallback
import org.jetbrains.anko.backgroundColor

import java.util.Arrays

/**
 * Created by cz on 16/1/23.
 */
class SimpleAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : BaseViewAdapter<E>(context, items), ViewScrollOffsetCallback {

    companion object {
        private val TAG = "SimpleAdapter"
        fun createFromResource(context: Context, @ArrayRes res: Int): SimpleAdapter<*> {
            return SimpleAdapter(context, context.resources.getStringArray(res))
        }
    }

    constructor(context: Context, items: Array<E>) : this(context, R.layout.simple_text_item, Arrays.asList(*items))

    constructor(context: Context, @LayoutRes layout: Int, items: Array<E>) : this(context, layout, Arrays.asList(*items))

    constructor(context: Context, items: List<E>) : this(context, R.layout.simple_text_item, items)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Log.e(TAG, "onCreateViewHolder")
        return BaseViewHolder(inflateView(parent, layout))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder:" + position)
        val textView = holder.itemView as TextView
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        Log.e(TAG, "onViewRecycled:" + holder.adapterPosition)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        Log.e(TAG, "onViewDetachedFromWindow:" + holder.adapterPosition)
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewScrollOffset(view: View, position: Int, centerPosition: Int, offset: Float) {
        Log.e(TAG,"onViewScrollOffset:$position centerPosition:$centerPosition offset:$offset")
        view.backgroundColor=evaluate(offset, Color.RED,Color.YELLOW)
    }

    private fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        val startInt = startValue
        val startA = startInt shr 24
        val startR = startInt shr 16 and 0xff
        val startG = startInt shr 8 and 0xff
        val startB = startInt and 0xff

        val endInt = endValue
        val endA = endInt shr 24
        val endR = endInt shr 16 and 0xff
        val endG = endInt shr 8 and 0xff
        val endB = endInt and 0xff

        return startA + (fraction * (endA - startA)).toInt() shl 24 or (startR + (fraction * (endR - startR)).toInt() shl 16) or (startG + (fraction * (endG - startG)).toInt() shl 8) or startB + (fraction * (endB - startB)).toInt()
    }

}
