package com.ldzs.recyclerlibrary.anim

import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class AnimateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun preAnimateAddImpl() {}

    fun preAnimateRemoveImpl() {}

    abstract fun animateAddImpl(listener: ViewPropertyAnimatorListener)

    abstract fun animateRemoveImpl(listener: ViewPropertyAnimatorListener)
}