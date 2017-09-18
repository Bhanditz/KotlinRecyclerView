package com.cz.sample.ui.layoutmanager.ui

import android.os.Bundle
import com.cz.recyclerlibrary.layoutmanager.base.BaseLayoutManager

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.SimpleLinearLayoutManager1
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_table.*

/**
 * Created by cz on 2017/1/21.
 */
@ToolBar
class SampleLinearLayoutManagerActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_layout_manager)
        setTitle(intent.getStringExtra("title"))
//        recyclerView.layoutManager = SimpleLinearLayoutManager()
        recyclerView.layoutManager = SimpleLinearLayoutManager1(SimpleLinearLayoutManager1.HORIZONTAL)
//        recyclerView.layoutManager =object :BaseLayoutManager(BaseLayoutManager.HORIZONTAL){}
        recyclerView.adapter = SimpleAdapter(this, (1..30).map { "Item:$it" })
    }
}
