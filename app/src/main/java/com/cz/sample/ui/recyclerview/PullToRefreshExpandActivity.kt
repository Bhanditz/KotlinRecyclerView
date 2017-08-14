package com.cz.sample.ui.recyclerview

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.adapter.FriendAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import com.cz.recyclerlibrary.onExpandItemClick
import com.cz.recyclerlibrary.onFooterRefresh
import com.cz.recyclerlibrary.onRefresh
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_expand_recycler_view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import java.util.Random

/**
 * Created by cz on 16/1/22.
 * 可展开的RecyclerView
 */
@ToolBar
class PullToRefreshExpandActivity : ToolBarActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_recycler_view)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.onExpandItemClick { v, groupPosition, childPosition ->
            Snackbar.make(v, getString(R.string.click_group_position, groupPosition, childPosition), Snackbar.LENGTH_LONG).show()
        }

        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addHeaderView(getHeaderView())

        recyclerView.addFooterView(getFooterView())
        recyclerView.addFooterView(getFooterView())

        var times=0
        recyclerView.onRefresh {
            times = 0
            recyclerView.postDelayed({
                addGroupItem(true)
                recyclerView.onRefreshComplete()
            }, 1000)
        }
        recyclerView.onFooterRefresh {
            if (times < 10) {
                recyclerView.postDelayed({
                    addGroupItem(false)
                    recyclerView.onRefreshComplete()
                }, 1000)
            } else {
                recyclerView.postDelayed({ recyclerView.setFooterRefreshDone() }, 1000)
            }
            times++
        }
        recyclerView.adapter=FriendAdapter(this, Data.createExpandItems(10, 10), true)
    }

    /**
     * 获得一个顶部控件
     */
    private fun getHeaderView(): View{
        val textColor = Data.randomColor
        val header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, findViewById(android.R.id.content) as ViewGroup, false)
        val headerView = header as TextView
        headerView.setTextColor(textColor)
        headerView.text = "HeaderView:" + recyclerView.headerViewCount
        headerView.onClick { recyclerView.addHeaderView(getHeaderView()) }
        return headerView
    }

    /**
     * 获得一个顶部控件
     */
    private fun getFooterView(): View{
        val textColor = Data.randomColor
        val footer = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, findViewById(android.R.id.content) as ViewGroup, false)
        val footerView = footer as TextView
        footerView.setTextColor(textColor)
        footerView.text = "HeaderView:" + recyclerView.headerViewCount
        return footerView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            addGroupItem(true)
            return true
        } else if (id == R.id.action_remove) {
            val adapter=recyclerView.adapter as FriendAdapter
            adapter.removeGroup(0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addGroupItem(top: Boolean) {
        val random = Random()
        val adapter=recyclerView.adapter as FriendAdapter
        adapter.addGroupItems(getString(R.string.add_group) + adapter.groupCount,
                Data.createItems(this, 2),
                if (top) 0 else adapter.groupCount, 0 == random.nextInt(2))
    }

}
