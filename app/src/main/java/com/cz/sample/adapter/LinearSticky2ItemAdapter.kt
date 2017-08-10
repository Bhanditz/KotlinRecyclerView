package com.cz.sample.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.cz.library.util.Utils
import com.cz.library.widget.FlowLayout
import com.cz.sample.R
import com.cz.sample.model.Sticky1Item
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.callback.Condition
import com.cz.recyclerlibrary.callback.StickyCallback
import com.cz.recyclerlibrary.strategy.GroupingStrategy
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/5/20.
 */

class LinearSticky2ItemAdapter(context: Context, items: List<Sticky1Item>) : BaseViewAdapter<Sticky1Item>(context, items), StickyCallback<Sticky1Item> {
    companion object {
        internal val ITEM_STICKY = 0
        internal val ITEM_NORMAL = 1
    }
    private val groupingStrategy = GroupingStrategy.of(this).reduce(Condition { !it.headerItems.isEmpty() })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder
        if (ITEM_STICKY == viewType) {
            holder = BaseViewHolder(inflateView(parent, R.layout.sticky_flow_item))
        } else {
            holder = BaseViewHolder(inflateView(parent, R.layout.sticky_text_item2))
        }
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getNonNullItem(position)
        val itemViewType = getItemViewType(position)
        if (ITEM_STICKY == itemViewType) {
            val flowLayout = holder.itemView.find<FlowLayout>(R.id.fl_sticky_layout)
            if (!item.headerItems.isEmpty()) {
                flowLayout.removeAllViews()
                val context = flowLayout.context
                for (text in item.headerItems) {
                    flowLayout.addView(getTextLabel(context, text))
                }
            }
        } else if (ITEM_NORMAL == itemViewType) {
            val textView = holder.itemView.find<TextView>(R.id.tv_view)
            textView.text = item.item
        }
    }

    override fun initStickyView(view: View, position: Int) {
        val groupStartIndex = groupingStrategy.getGroupStartIndex(position)
        val item = getNonNullItem(groupStartIndex)
        val flowLayout = view.findViewById(R.id.fl_sticky_layout) as FlowLayout
        flowLayout.layoutTransition = null
        if (!item.headerItems.isEmpty()) {
            flowLayout.removeAllViews()
            val context = view.context
            for (text in item.headerItems) {
                flowLayout.addView(getTextLabel(context, text))
            }
        }
    }

    override fun getGroupingStrategy(): GroupingStrategy<Sticky1Item> =groupingStrategy

    private fun getTextLabel(context: Context, text: String): TextView {
        val textView = Button(context)
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.setTextColor(ContextCompat.getColorStateList(context, R.drawable.text2white_select_text))
        textView.setPadding(Utils.dip2px(12f), Utils.dip2px(4f), Utils.dip2px(12f), Utils.dip2px(4f))
        return textView
    }

    override fun getItemViewType(position: Int): Int {
        val item = getNonNullItem(position)
        var viewType = ITEM_NORMAL
        if (!item.headerItems.isEmpty()) {
            viewType = ITEM_STICKY
        }
        return viewType
    }


}
