package com.cz.sample.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.expand.ExpandAdapter

/**
 * Created by cz on 16/1/22.
 */
class FriendAdapter(context: Context, items: MutableList<ExpandAdapter.Entry<String, List<String>>>, expand: Boolean) : ExpandAdapter<String, String>(context, items, expand) {


    override fun createGroupHolder(parent: ViewGroup): BaseViewHolder {
        return GroupHolder(inflateView(parent, R.layout.group_item))
    }

    override fun createChildHolder(parent: ViewGroup): BaseViewHolder {
        return ItemHolder(inflateView(parent, R.layout.text_item))
    }

    override fun onBindGroupHolder(holder: BaseViewHolder, groupPosition: Int) {
        val groupHolder = holder as GroupHolder
        groupHolder.imageFlag.isSelected = getGroupExpand(groupPosition)//当前分组展开状态
        groupHolder.textView.text = getGroup(groupPosition)
        groupHolder.count.text = "(" + getChildrenCount(groupPosition) + ")"//子孩子个数
    }

    override fun onBindChildHolder(holder: BaseViewHolder, groupPosition: Int, childPosition: Int) {
        val itemHolder = holder as ItemHolder
        val item = getChild(groupPosition, childPosition)
        itemHolder.textView.text = item
    }

    override fun onGroupExpand(holder: BaseViewHolder, expand: Boolean, groupPosition: Int) {
        super.onGroupExpand(holder, expand, groupPosition)
        val groupHolder = holder as GroupHolder
        groupHolder.imageFlag.isSelected = expand
    }

    class GroupHolder(itemView: View) : BaseViewHolder(itemView) {
        var imageFlag: ImageView = itemView.findViewById(R.id.iv_group_flag) as ImageView
        var textView: TextView = itemView.findViewById(R.id.tv_group_name) as TextView
        var count: TextView = itemView.findViewById(R.id.tv_group_count) as TextView

    }

    class ItemHolder(itemView: View) : BaseViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text) as TextView

    }

}
