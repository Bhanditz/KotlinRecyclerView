package com.cz.sample.ui.layoutmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cz.recyclerlibrary.layoutmanager.viewpager.ViewPager

import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.adapter.GalleryImageAdapter
import com.cz.sample.ui.layoutmanager.adapter.ViewPagerImageAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_view_pager.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import java.util.ArrayList

@ToolBar
class ViewPagerActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        setTitle(intent.getStringExtra("title"))

        val items = ArrayList<Int>()
        items.add(R.mipmap.gallery_1)
        items.add(R.mipmap.gallery_2)
        items.add(R.mipmap.gallery_3)
        //设置最小动化变化速率为0.8
        viewPager.setMinScrollOffset(0.8f)
        //设置最小可滚动个数为2个
//        recyclerView.setCycleCount(1)
        viewPager.adapter = ViewPagerImageAdapter(this, items)
//        viewPager.onSelectPositionChanged { _, i, _ -> text.text="Position:$i" }
//
//        cycleCheckBox.onCheckedChange { _, isChecked -> gallery.setCycle(isChecked) }

        viewPager.onSelectPositionChanged { _, i, _ -> text.text="Position:$i" }

        cycleCheckBox.onCheckedChange { _, isChecked -> viewPager.setCycle(isChecked) }

        orientationLayout.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.horizontal->viewPager.setOrientation(ViewPager.HORIZONTAL)
                else->viewPager.setOrientation(ViewPager.VERTICAL)
            }
        }
    }
}
