package com.cz.recyclerlibrary.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

import com.cz.recyclerlibrary.callback.GridSpanCallback
import com.cz.recyclerlibrary.callback.MultiStickyCallback
import com.cz.recyclerlibrary.callback.StickyCallback

/**
 * Created by Administrator on 2017/5/20.
 */
abstract class GridMultiStickyAdapter<E>(context: Context, items: List<E>?) : BaseViewAdapter<E>(context, items), MultiStickyCallback<E>, GridSpanCallback {

    override fun getSpanSize(layoutManager: RecyclerView.LayoutManager, position: Int): Int {
        var spanCount = 1
        val groupingStrategy = getGroupingStrategy()
        if (layoutManager is GridLayoutManager && groupingStrategy.isGroupIndex(position)) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }
}
