package com.cz.sample.ui.drag

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import com.cz.sample.onDragItemEnable
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_drag.*

/**
 * Created by cz on 16/1/27.
 */
@ToolBar
class DynamicDragActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        recyclerView.adapter = SimpleAdapter(this, R.layout.grid_text_item, Data.createItems(this, 100))
        recyclerView.setLongPressDrawEnable(true)
        recyclerView.onDragItemEnable { true }

        //添加指定位置view
        recyclerView.addDynamicView(Data.getHeaderItemView(this), 3)
        recyclerView.addDynamicView(Data.getHeaderItemView(this), 10)
    }


}
