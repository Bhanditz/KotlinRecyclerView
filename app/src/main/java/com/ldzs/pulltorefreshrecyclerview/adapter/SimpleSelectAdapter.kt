package com.ldzs.pulltorefreshrecyclerview.adapter

import android.content.Context
import android.support.annotation.ArrayRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.recyclerlibrary.adapter.BaseViewAdapter
import com.ldzs.recyclerlibrary.adapter.BaseViewHolder
import com.ldzs.recyclerlibrary.adapter.CacheViewHolder
import com.ldzs.recyclerlibrary.callback.Selectable

import java.util.Arrays

/**
 * Created by cz on 16/1/23.
 * @warning select adapter must be implement Selectable
 * *
 * @see com.ldzs.recyclerlibrary.callback.Selectable
 */
class SimpleSelectAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : BaseViewAdapter<E>(context, items), Selectable<BaseViewHolder> {

    constructor(context: Context, items: Array<E>) : this(context, R.layout.simple_text_item, Arrays.asList(*items)) {}

    constructor(context: Context, @LayoutRes layout: Int, items: Array<E>) : this(context, layout, Arrays.asList(*items)) {}

    constructor(context: Context, items: List<E>) : this(context, R.layout.simple_text_item, items) {}

    override fun onSelectItem(holder: BaseViewHolder, position: Int, select: Boolean) {
        holder.itemView.isSelected = select
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CacheViewHolder(inflateView(parent, layout))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
    }

    companion object {
        fun createFromResource(context: Context, @ArrayRes res: Int): SimpleSelectAdapter<*> {
            return SimpleSelectAdapter(context, context.resources.getStringArray(res))
        }
    }


}
