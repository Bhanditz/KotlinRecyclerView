package com.cz.sample.ui.layoutmanager.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.layoutmanager.table.TableAdapter
import com.cz.recyclerlibrary.layoutmanager.table.TableColumnLayout
import com.cz.sample.R
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick


/**
 * Created by cz on 2017/1/21.
 */

class TableItemAdapter(context: Context, items: List<String>) : TableAdapter<String>(context, items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(inflateView(parent, R.layout.table_item))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val viewGroup = holder.itemView as ViewGroup
        val childCount = viewGroup.childCount
        for (i in 0..childCount - 1) {
            val textView = viewGroup.getChildAt(i) as TextView
            textView.onClick {
                Toast.makeText(it?.context,"点击$position $i!",Toast.LENGTH_SHORT).show()
            }
            textView.text = item + " column:" + i
        }
    }

    override fun getHeaderItemView(headerLayout:TableColumnLayout,index:Int): View {
        return inflateView(headerLayout, R.layout.table_header_item)
    }

    override fun onBindHeaderView(headerLayout: TableColumnLayout) {
        super.onBindHeaderView(headerLayout)
        headerLayout.backgroundColor=Color.RED
    }
    override fun onBindHeaderItemView(parent: TableColumnLayout, view: View, column: Int) {
        val childView=view.find<TextView>(R.id.text)
        childView.text="Header$column"
    }
}
