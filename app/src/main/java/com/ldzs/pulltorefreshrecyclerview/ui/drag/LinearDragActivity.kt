package com.ldzs.pulltorefreshrecyclerview.ui.drag

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.onDragItemEnable
import com.ldzs.recyclerlibrary.DragRecyclerView
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator
import com.ldzs.recyclerlibrary.callback.OnDragItemEnableListener
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_drag.*

/**
 * Created by cz on 16/1/27.
 */
@ToolBar
class LinearDragActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SimpleAdapter(this, Data.createItems(this, 100))
        recyclerView.setLongPressDrawEnable(true)
        recyclerView.onDragItemEnable { true }
    }
}
