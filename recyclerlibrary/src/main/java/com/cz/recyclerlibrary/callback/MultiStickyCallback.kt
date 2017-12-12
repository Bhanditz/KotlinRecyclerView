package com.cz.recyclerlibrary.callback

import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.strategy.GroupingStrategy

/**
 * Created by cz on 2017/12/21.
 * 多分组的sticky回调对象
 */
interface MultiStickyCallback<T>:StickyCallback<T> {
    /**
     * 获得当前位置的sticky 分类
     */
    fun getStickyViewType(position: Int):Int

    /**
     * 获得初始化sticky 控件
     */
    fun getStickyView(parent:ViewGroup,viewType:Int):View
}
