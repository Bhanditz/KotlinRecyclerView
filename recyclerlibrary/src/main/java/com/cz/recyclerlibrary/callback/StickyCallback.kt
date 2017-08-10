package com.cz.recyclerlibrary.callback

import android.view.View

import com.cz.recyclerlibrary.strategy.GroupingStrategy

/**
 * Created by Administrator on 2017/5/20.
 */

interface StickyCallback<T> {
    fun initStickyView(view: View, position: Int)

    fun getGroupingStrategy(): GroupingStrategy<T>
}
