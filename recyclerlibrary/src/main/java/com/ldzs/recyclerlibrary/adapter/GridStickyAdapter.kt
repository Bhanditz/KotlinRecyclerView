package com.ldzs.recyclerlibrary.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

import com.ldzs.recyclerlibrary.callback.GridSpanCallback
import com.ldzs.recyclerlibrary.callback.StickyCallback
import com.ldzs.recyclerlibrary.strategy.GroupingStrategy

/**
 * Created by Administrator on 2017/5/20.
 */
abstract class GridStickyAdapter<E>(context: Context, items: List<E>) : BaseViewAdapter<E>(context, items), StickyCallback<E>, GridSpanCallback {

    override fun getSpanSize(layoutManager: RecyclerView.LayoutManager, position: Int): Int {
        var spanCount = 1
        val groupingStrategy = getGroupingStrategy()
        if (layoutManager is GridLayoutManager && groupingStrategy.isGroupIndex(position)) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }
}
