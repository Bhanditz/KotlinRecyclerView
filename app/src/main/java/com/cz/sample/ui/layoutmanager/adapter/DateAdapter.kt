package com.cz.sample.ui.layoutmanager.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.layoutmanager.base.ViewScrollOffsetCallback
import com.cz.sample.R

import java.text.DecimalFormat


/**
 * Created by cz on 1/18/17.
 */

class DateAdapter(context: Context, numberFormatValue: String?, items: List<Int>?) : BaseViewAdapter<Int>(context, items), ViewScrollOffsetCallback {
    private var formatter: DecimalFormat? = null

    init {
        if (!TextUtils.isEmpty(numberFormatValue)) {
            this.formatter = DecimalFormat(numberFormatValue)
        }
    }

    override fun onViewScrollOffset(view: View, position: Int, centerPosition: Int, offset: Float) {
        val textView = view as TextView
        textView.alpha = offset
        textView.scaleX = offset
        textView.scaleY = offset
        textView.rotationX = 45 * (1f - offset)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflateView(parent, R.layout.date_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        if (null != formatter) {
            textView.text = formatter!!.format(getItem(position))
        } else {
            textView.text = getItem(position).toString()
        }
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
