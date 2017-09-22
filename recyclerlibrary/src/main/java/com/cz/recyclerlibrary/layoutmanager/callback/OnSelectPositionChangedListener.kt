package com.cz.recyclerlibrary.layoutmanager.callback

import android.view.View

/**
 * 当中间滚动位置发生变化回调事件
 */
interface OnSelectPositionChangedListener {
    fun onSelectPositionChanged(view: View?, position:Int,lastPosition:Int)
}