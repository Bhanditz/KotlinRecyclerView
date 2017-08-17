package com.cz.sample.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.BinaryCondition
import com.cz.recyclerlibrary.callback.DividerInterceptCallback
import com.cz.recyclerlibrary.callback.StickyCallback
import com.cz.recyclerlibrary.strategy.GroupingStrategy
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/5/20.
 */

class LinearSticky1ItemAdapter(context: Context, items: List<String>) : BaseViewAdapter<String>(context, items), StickyCallback<String>,DividerInterceptCallback {

    private val groupingStrategy= GroupingStrategy.of(this).reduce{ t1, t2 -> t1[0] != t2[0] }

    constructor(parcel: Parcel) : this(
            TODO("context"),
            TODO("items")) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflateView(parent, R.layout.sticky_text_item1))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        val stickyView = holder.itemView.find<TextView>(R.id.tv_sticky_view)
        val textView = holder.itemView.find<TextView>(R.id.tv_view)
        val isStickyPosition = groupingStrategy.isGroupIndex(position)
        stickyView.visibility = if (isStickyPosition) View.VISIBLE else View.GONE
        if (isStickyPosition) {
            stickyView.text = item[0].toString()
        }
        textView.text = item
    }

    override fun initStickyView(view: View, position: Int) {
        val item = getItem(position)
        val textView = view as TextView
        if (!TextUtils.isEmpty(item)) {
            textView.text = item[0].toString()
        }
    }

    override fun getGroupingStrategy(): GroupingStrategy<String> =groupingStrategy

    override fun intercept(position: Int): Boolean =groupingStrategy.isGroupIndex(position)


}
