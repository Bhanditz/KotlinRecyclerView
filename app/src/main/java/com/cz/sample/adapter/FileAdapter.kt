package com.cz.sample.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.tree.TreeAdapter
import org.jetbrains.anko.find

import java.io.File

/**
 * Created by cz on 16/1/23.
 */
class FileAdapter(context: Context, rootNode: TreeAdapter.TreeNode<File>) : TreeAdapter<File>(context, rootNode) {
    private val PADDING: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()

    override fun onBindViewHolder(holder: BaseViewHolder, node: TreeAdapter.TreeNode<File>, file: File, viewType: Int, position: Int) {
        val itemView = holder.itemView
        itemView.setPadding(PADDING * node.level, itemView.paddingTop, itemView.paddingRight, itemView.paddingBottom)
        when (viewType) {
            FILE_ITEM -> {
                holder.itemView.find<TextView>(R.id.tv_simple_name).text = getSimpleName(file.name)
                holder.itemView.find<TextView>(R.id.tv_name).text = file.name
            }
            FOLDER_ITEM -> {
                holder.itemView.find<TextView>(R.id.tv_simple_name).text = getSimpleName(file.name)
                holder.itemView.find<TextView>(R.id.tv_name).text = file.name + "(" + node.child.size + ")"
                holder.itemView.find<View>(R.id.iv_flag).isSelected = node.expand
            }
        }
    }

    override fun onNodeExpand(node: TreeAdapter.TreeNode<File>, holder: BaseViewHolder, expand: Boolean) {
        super.onNodeExpand(node, holder, expand)
        holder.itemView.find<View>(R.id.iv_flag).isSelected = expand
    }

    private fun getSimpleName(name: String): String {
        return name.substring(0, Math.min(2, name.length))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            FOLDER_ITEM -> BaseViewHolder(createView(parent, R.layout.folder_item))
            else -> BaseViewHolder(createView(parent, R.layout.file_item))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val file = getItem(position)
        var viewType = FILE_ITEM
        if (file.isDirectory) {
            viewType = FOLDER_ITEM
        }
        return viewType
    }

    companion object {
        private val FOLDER_ITEM = 0
        private val FILE_ITEM = 1
    }

}
