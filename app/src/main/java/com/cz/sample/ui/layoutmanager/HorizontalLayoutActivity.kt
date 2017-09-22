package com.cz.sample.ui.layoutmanager

import android.os.Bundle
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_horizontal_layout_manager.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by cz on 2017/1/21.
 */
@ToolBar
class HorizontalLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal_layout_manager)
        setTitle(intent.getStringExtra("title"))
        val layoutManager=CenterLinearLayoutManager(BaseLinearLayoutManager.HORIZONTAL)
        recyclerView.layoutManager = layoutManager
        val adapter=SimpleAdapter(this,R.layout.horizontal_text_item, (1..30).map { "Item:$it" })
        recyclerView.adapter =adapter
        cycleCheckBox.onCheckedChange { _, isChecked ->  layoutManager.cycle=isChecked}
        scrollButton.onClick {
            recyclerView.smoothScrollToPosition(30)
        }
    }
}
