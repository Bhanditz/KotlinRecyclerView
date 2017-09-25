package com.cz.sample.ui.layoutmanager

import android.os.Bundle
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_vertical_layout_manager.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * Created by cz on 2017/1/21.
 */
@ToolBar
class VerticalLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_layout_manager)
        setTitle(intent.getStringExtra("title"))
        val layoutManager=CenterLinearLayoutManager(BaseLinearLayoutManager.VERTICAL)
        layoutManager.setMinScrollOffset(0.6f)
        recyclerView.layoutManager = layoutManager
        val adapter=SimpleAdapter(this, (0..30).map { "Item:$it" })
        recyclerView.adapter =adapter
        cycleCheckBox.onCheckedChange { _, isChecked ->  layoutManager.cycle=isChecked}
        scrollButton.onClick {
            recyclerView.smoothScrollToPosition(30)
        }

        val random= Random()
        addButton.onClick {
            val index=random.nextInt(adapter.itemsCount)
            adapter.addItemNotify("NewItem${adapter.itemCount}",5)
        }
    }
}
