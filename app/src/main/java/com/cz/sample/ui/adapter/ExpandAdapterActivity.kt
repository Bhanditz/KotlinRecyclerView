package com.cz.sample.ui.adapter

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.recyclerlibrary.adapter.drag.DynamicAdapter
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_full_adapter.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * Created by cz on 16/1/24.
 */
@ToolBar
class ExpandAdapterActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_adapter)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = DynamicAdapter(SimpleAdapter(this, Data.createItems(this, 150)))
        recyclerView.adapter = adapter
        val random = Random()

        val viewItems = LinkedList<View>()
        buttonAdd.onClick {
            val itemView = getFullItemView(adapter)
            viewItems.offerLast(itemView)
            adapter.addDynamicView(itemView, random.nextInt(adapter.itemCount))
        }
        buttonRemove.onClick { adapter.removeDynamicView(viewItems.pollFirst()) }
    }

    /**
     * 获得一个铺满的控件
     */
    private fun getFullItemView(adapter:DynamicAdapter): View{
        val color = Data.randomColor
        val darkColor = Data.getDarkColor(color)
        val header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, findViewById(android.R.id.content) as ViewGroup, false)
        val headerView = header as TextView
        header.setBackgroundColor(color)
        headerView.setTextColor(darkColor)
        headerView.text = "HeaderView:" + adapter.dynamicItemCount
        return headerView
    }

}
