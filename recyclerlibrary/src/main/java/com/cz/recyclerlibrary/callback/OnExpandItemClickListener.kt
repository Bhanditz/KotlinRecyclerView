package com.cz.recyclerlibrary.callback

import android.view.View


interface OnExpandItemClickListener {
    fun onItemClick(v: View, groupPosition: Int, childPosition: Int)
}