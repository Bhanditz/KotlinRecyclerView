package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.FriendAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.onExpandItemClick
import com.ldzs.pulltorefreshrecyclerview.onFooterRefresh
import com.ldzs.pulltorefreshrecyclerview.onRefresh
import com.ldzs.recyclerlibrary.PullToRefreshExpandRecyclerView
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator
import com.ldzs.recyclerlibrary.callback.OnExpandItemClickListener
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
        recyclerView.setAdapter(FriendAdapter(this, Data.createExpandItems(10, 10), true))
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
        val adapter=recyclerView.getAdapter() as FriendAdapter
        if (id == R.id.action_add) {
            addGroupItem(true)
            return true
        } else if (id == R.id.action_remove) {
            adapter.removeGroup(0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addGroupItem(top: Boolean) {
        val random = Random()
        val adapter=recyclerView.getAdapter() as FriendAdapter
        adapter.addGroupItems(getString(R.string.add_group) + adapter.groupCount,
                Data.createItems(this, 2),
                if (top) 0 else adapter.groupCount, 0 == random.nextInt(2))
    }

}
