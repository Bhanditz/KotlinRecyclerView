package com.cz.sample.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.model.Sticky2Item
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.GridMultiStickyAdapter
import com.cz.recyclerlibrary.adapter.GridStickyAdapter
import com.cz.recyclerlibrary.callback.Condition
import com.cz.recyclerlibrary.strategy.GroupingStrategy
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/5/20.
 */

class GridStickyItem3Adapter(context: Context, items: List<Sticky2Item>) : GridMultiStickyAdapter<Sticky2Item>(context, items) {

    companion object {
        internal val ITEM_STICKY = 0
        internal val ITEM_NORMAL = 1

        internal val HEADER_ITEM1=0
        internal val HEADER_ITEM2=1
    }
    private val groupingStrategy = GroupingStrategy.of(this).reduce { item-> item.title }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewType = getItemViewType(position)
        if (ITEM_STICKY == itemViewType) {
            val textView = holder.itemView.find<TextView>(R.id.tv_sticky_view)
            textView.text = item.item
        } else if (ITEM_NORMAL == itemViewType) {
            holder.itemView.find<TextView>(R.id.tv_text).text = item.item
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder
        if (ITEM_STICKY == viewType) {
            holder = BaseViewHolder(inflateView(parent, R.layout.sticky_top_item))
        } else {
            holder = BaseViewHolder(inflateView(parent, R.layout.grid_image_item))
        }
        return holder
    }

    override fun getStickyViewType(position: Int): Int {
        var viewType=HEADER_ITEM1
        if(0==position){
            viewType=HEADER_ITEM2
        }
        return viewType
    }

    override fun getStickyView(parent: ViewGroup, viewType: Int): View {
        return when(viewType){
            HEADER_ITEM1->inflater.inflate(R.layout.sticky_item1,parent,false)
            else->inflater.inflate(R.layout.sticky_item2,parent,false)
        }
    }

    override fun initStickyView(view: View, position: Int) {
        val item = getItem(position)
        val stickyViewType = getStickyViewType(position)
        if(HEADER_ITEM1==stickyViewType){
            val stickyView = view.findViewById(R.id.stickyView1) as TextView
            stickyView.text = item.item
        } else if(HEADER_ITEM2==stickyViewType){
            val stickyView = view.findViewById(R.id.stickyView) as TextView
            stickyView.text = item.item
        }
    }

    override fun getGroupingStrategy(): GroupingStrategy<Sticky2Item> =groupingStrategy

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.title) ITEM_STICKY else ITEM_NORMAL
    }



}
