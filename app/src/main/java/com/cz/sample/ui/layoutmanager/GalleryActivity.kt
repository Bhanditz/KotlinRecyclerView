package com.cz.sample.ui.layoutmanager

import android.os.Bundle
import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.adapter.GalleryImageAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_gallery.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange

import java.util.ArrayList

/**
 * Created by cz on 1/18/17.
 */
@ToolBar
class GalleryActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setTitle(intent.getStringExtra("title"))
        val items = ArrayList<Int>()
        items.add(R.mipmap.gallery_1)
        items.add(R.mipmap.gallery_2)
//        items.add(R.mipmap.gallery_3)
        //设置最小动化变化速率为0.8
        gallery.setMinScrollOffset(0.8f)
        //设置最小可滚动个数为2个
//        recyclerView.setCycleCount(1)
        gallery.adapter = GalleryImageAdapter(this, items)
        gallery.onSelectPositionChanged { _, i, _ -> text.text="Position:$i" }

        cycleCheckBox.onCheckedChange { _, isChecked -> gallery.setCycle(isChecked) }
    }
}
