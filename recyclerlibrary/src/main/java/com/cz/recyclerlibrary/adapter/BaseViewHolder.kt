package com.cz.recyclerlibrary.adapter

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * 数据Holder对象
 */
open class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    open fun view(@IdRes id: Int): View =itemView.findViewById(id)

    open fun textView(@IdRes id: Int): TextView =itemView.findViewById(id) as TextView

    open fun imageView(@IdRes id: Int): ImageView =itemView.findViewById(id)as ImageView

}