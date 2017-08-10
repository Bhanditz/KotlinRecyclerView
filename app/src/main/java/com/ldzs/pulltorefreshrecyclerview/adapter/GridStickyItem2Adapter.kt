package com.ldzs.pulltorefreshrecyclerview.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.model.Sticky2Item
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder
import com.ldzs.recyclerlibrary.adapter.GridStickyAdapter
import com.ldzs.recyclerlibrary.callback.Condition
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/5/20.
 */

class GridStickyItem2Adapter(context: Context, items: List<Sticky2Item>) : GridStickyAdapter<Sticky2Item>(context, items) {
    companion object {
        internal val ITEM_STICKY = 0
        internal val ITEM_NORMAL = 1
    }
    private val groupingStrategy = GroupingStrategy.of(this).reduce(Condition { it.title })

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getNonNullItem(position)
        val itemViewType = getItemViewType(position)
        if (ITEM_STICKY == itemViewType) {
            val textView = holder.itemView.find<TextView>(R.id.tv_sticky_view)
            textView.text = item.item
        } else if (ITEM_NORMAL == itemViewType) {
            holder.itemView.find<TextView>(R.id.tv_text).text = item.item
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: CacheViewHolder
        if (ITEM_STICKY == viewType) {
            holder = CacheViewHolder(inflateView(parent, R.layout.sticky_top_item))
        } else {
            holder = CacheViewHolder(inflateView(parent, R.layout.grid_image_item))
        }
        return holder
    }


    override fun initStickyView(view: View, position: Int) {
        val item = getNonNullItem(position)
        val stickyView = view.findViewById(R.id.tv_sticky_view) as TextView
        stickyView.text = item.item
    }

    override fun getGroupingStrategy(): GroupingStrategy<Sticky2Item> =groupingStrategy

    override fun getItemViewType(position: Int): Int {
        val item = getNonNullItem(position)
        return if (item.title) ITEM_STICKY else ITEM_NORMAL
    }



}
