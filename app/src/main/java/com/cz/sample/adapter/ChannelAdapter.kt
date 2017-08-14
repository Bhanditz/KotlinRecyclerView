package com.cz.sample.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.model.Channel
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import org.jetbrains.anko.find

/**
 * Created by cz on 16/1/27.
 */
class ChannelAdapter(context: Context, items: List<Channel>) : BaseViewAdapter<Channel>(context, items) {
    private var dragStatus: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflateView(parent, R.layout.channel_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.find<TextView>(R.id.tv_name).text = item.name
        holder.itemView.find<View>(R.id.iv_flag).visibility = if (item.use) View.GONE else View.VISIBLE
        holder.itemView.find<View>(R.id.iv_delete_icon).visibility = if (dragStatus && item.use) View.VISIBLE else View.GONE
    }

    /**
     * 设置当前拖动状态

     * @param drag
     */
    fun setDragStatus(drag: Boolean) {
        this.dragStatus = drag
        notifyItemRangeChanged(0, itemsCount)
    }
}
