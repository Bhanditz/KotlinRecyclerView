package com.cz.sample.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.model.Sticky2Item
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.GridStickyAdapter
import com.cz.recyclerlibrary.callback.Condition
import com.cz.recyclerlibrary.strategy.GroupingStrategy
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/5/20.
 */

class GridStickyItem1Adapter(context: Context, items: List<Sticky2Item>) : GridStickyAdapter<Sticky2Item>(context, items) {
    companion object {
        internal val ITEM_STICKY = 0
        internal val ITEM_NORMAL = 1
    }
    internal val groupingStrategy= GroupingStrategy.of(this).reduce(Condition{ it.title})

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewType = getItemViewType(position)
        if (ITEM_STICKY == itemViewType) {
            val textView = holder.itemView.find<TextView>(R.id.tv_sticky_view)
            textView.text = item.item
        } else if (ITEM_NORMAL == itemViewType) {
            val textView = holder.itemView.find<TextView>(R.id.tv_view)
            textView.text = item.item
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder
        if (ITEM_STICKY == viewType) {
            holder = BaseViewHolder(inflateView(parent, R.layout.sticky_top_item))
        } else {
            holder = BaseViewHolder(inflateView(parent, R.layout.sticky_text_item2))
        }
        return holder
    }


    override fun initStickyView(view: View, position: Int) {
        val item = getItem(position)
        val stickyView = view.findViewById(R.id.tv_sticky_view) as TextView
        stickyView.text = item.item
    }

    override fun getGroupingStrategy(): GroupingStrategy<Sticky2Item> =groupingStrategy

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.title) ITEM_STICKY else ITEM_NORMAL
    }


}
