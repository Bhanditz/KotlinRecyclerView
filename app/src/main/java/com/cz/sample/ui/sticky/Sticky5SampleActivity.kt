package com.cz.sample.ui.sticky

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager

import com.cz.sample.R
import com.cz.sample.adapter.GridStickyItem2Adapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.sample.model.Sticky2Item
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_sticky3.*

import java.util.ArrayList

/**
 * Created by cz on 2017/6/9.
 */
@ToolBar
class Sticky5SampleActivity : ToolBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky3)
        setTitle(intent.getStringExtra("title"))
        val items = ArrayList<Sticky2Item>()
        var lastItem: String? = null
        for (item in Data.ITEMS) {
            val firstItem = item[0].toString()
            if (null == lastItem || lastItem != firstItem) {
                items.add(Sticky2Item(true, firstItem))
            }
            items.add(Sticky2Item(false, item))
            lastItem = item[0].toString()
        }
        refreshStickyRecyclerView.layoutManager = GridLayoutManager(this, 3)
        refreshStickyRecyclerView.adapter=GridStickyItem2Adapter(this, items)

    }
}
