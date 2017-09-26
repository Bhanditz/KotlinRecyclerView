package com.cz.recyclerlibrary.layoutmanager.table

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created by cz on 2017/1/20.
 * 一个表格控件
 */

class TableView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        super.setLayoutManager(TableLayoutManager())
        super.setAdapter(adapter)
    }

    /**
     * @param layout
     */
    @Deprecated(" ")
    override fun setLayoutManager(layout: RecyclerView.LayoutManager) {
    }


}
