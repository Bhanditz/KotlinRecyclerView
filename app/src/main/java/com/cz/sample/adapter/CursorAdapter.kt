package com.cz.sample.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import com.cz.recyclerlibrary.adapter.CursorRecyclerAdapter

/**
 * Created by cz on 16/3/15.
 */
class CursorAdapter(context: Context, cursor: Cursor) : CursorRecyclerAdapter<BaseViewHolder>(context, cursor) {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onBindViewHolder(holder: BaseViewHolder, cursor: Cursor, position: Int) {
        val textView = holder.itemView as TextView
        val text = cursor.getString(1)
        textView.text = text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(layoutInflater.inflate(R.layout.simple_text_item, parent, false))
    }
}
