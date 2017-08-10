package com.ldzs.pulltorefreshrecyclerview.ui.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.onItemClick
import com.ldzs.recyclerlibrary.adapter.drag.DynamicAdapter
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator
import com.ldzs.recyclerlibrary.callback.OnItemClickListener
import com.ldzs.recyclerlibrary.observe.DynamicAdapterDataObserve
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_full_adapter.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import java.util.ArrayList
import java.util.LinkedList
import java.util.Random

/**
 * Created by cz on 16/1/24.
 */
@ToolBar
class DynamicAdapterActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_adapter)
        setTitle(intent.getStringExtra("title"))

        val viewItems = LinkedList<View>()
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val simpleAdapter=SimpleAdapter(this, Data.createItems(this, 100))
        val adapter = DynamicAdapter(simpleAdapter)
        adapter.onItemClick { _, position -> Toast.makeText(this, "position:" + position, Toast.LENGTH_SHORT).show() }
        simpleAdapter.registerAdapterDataObserver(DynamicAdapterDataObserve(adapter))
        recyclerView.adapter = adapter
        val random = Random()
        findViewById(R.id.buttonAdd).onClick {
            val itemCount = adapter.itemCount
            simpleAdapter.addItemNotify("new:" + adapter.itemCount, random.nextInt(if (0 == itemCount) 1 else itemCount))
        }
        findViewById(R.id.buttonRemove).onClick {
            if (0 != adapter.itemCount) {
                simpleAdapter.removeNotifyItem(0, 8)
            }
        }
        findViewById(R.id.buttonGlobalRemove).onClick {
            simpleAdapter.remove(0, 8)
            adapter.itemRangeGlobalRemoved(0, 8)
        }
        findViewById(R.id.buttonRandomAdd).onClick { addView(adapter,viewItems,random.nextInt(adapter.itemCount)) }
        findViewById(R.id.buttonRandomRemove).onClick {
            if (!viewItems.isEmpty()) {
                adapter.removeDynamicView(viewItems.pollFirst())
            }
        }
    }

    private fun addView(adapter:DynamicAdapter,viewItems:LinkedList<View>,position: Int) {
        val view = getFullItemView(adapter)
        viewItems.add(view)
        adapter.addDynamicView(view, position)
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
