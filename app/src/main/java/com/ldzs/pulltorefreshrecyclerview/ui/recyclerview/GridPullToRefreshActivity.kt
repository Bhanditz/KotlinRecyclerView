package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.onFooterRefresh
import com.ldzs.pulltorefreshrecyclerview.onItemClick
import com.ldzs.pulltorefreshrecyclerview.onRefresh
import com.ldzs.pulltorefreshrecyclerview.widget.RadioLayout
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView
import com.ldzs.recyclerlibrary.callback.OnItemClickListener
import cz.refreshlayout.library.BasePullToRefreshLayout
import cz.refreshlayout.library.RefreshMode

import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_grid_recycler_view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar
class GridPullToRefreshActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_recycler_view)
        setTitle(intent.getStringExtra("title"))


        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter=SimpleAdapter(this, R.layout.grid_text_item, Data.createItems(this, 10))
        recyclerView.setAdapter(adapter)
        recyclerView.onItemClick { v, position -> Snackbar.make(v, getString(R.string.click_position, position), Snackbar.LENGTH_LONG).show() }

        refreshModeLayout.setOnCheckedListener(object :RadioLayout.OnCheckedListener{
            override fun onChecked(v: View, position: Int, isChecked: Boolean) {
                recyclerView.setRefreshMode(when (position) {
                    0 ->  RefreshMode.BOTH
                    1 -> RefreshMode.PULL_START
                    2 -> RefreshMode.PULL_END
                    else -> RefreshMode.DISABLED
                })
            }

        })
        recyclerView.onRefresh {
            recyclerView.postDelayed({
                adapter.addItemsNotify(Data.createItems(this, 10), 0)
                recyclerView.onRefreshComplete()
            }, 1000)
        }

        var times=0
        recyclerView.onFooterRefresh {
            if (times < 2) {
                recyclerView.postDelayed({
                    adapter.addItemsNotify(Data.createItems(this, 10))
                    recyclerView.onRefreshFootComplete()
                }, 1000)
            } else {
                recyclerView.postDelayed({ recyclerView.setFooterRefreshDone() }, 1000)
            }
            times++
        }

        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addFooterView(getHeaderView())
        recyclerView.addFooterView(getHeaderView())
        recyclerView.addFooterView(getHeaderView())

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
        headerView.onClick { recyclerView.addHeaderView(headerView) }
        return headerView
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val adapter=recyclerView as SimpleAdapter<String>
        if (id == R.id.action_add) {
            adapter.addItem(getString(R.string.header), 0)
            return true
        } else if (id == R.id.action_remove) {
            adapter.remove(0)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
