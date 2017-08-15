package com.cz.recyclerlibrary.callback

import android.support.v7.widget.RecyclerView

/**
 * Created by czz on 2016/9/15.
 */
interface Selectable<in VH : RecyclerView.ViewHolder> {
    fun onSelectItem(holder: VH, position: Int, select: Boolean)
}
