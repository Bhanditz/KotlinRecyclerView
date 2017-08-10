package com.ldzs.pulltorefreshrecyclerview.ui.recyclerview

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ldzs.pulltorefreshrecyclerview.*

import com.ldzs.pulltorefreshrecyclerview.adapter.SimpleAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.widget.RadioLayout
import com.ldzs.recyclerlibrary.PullToRefreshRecyclerView
import com.ldzs.recyclerlibrary.anim.SlideInLeftAnimator

import cz.refreshlayout.library.RefreshMode
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_linear_recycler_view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * 1:示例添加头,添加信息,以及自定义的Adapter使用.
 * 2:示例底部加载情况,加载中/加载异常/加载完毕
 */
@ToolBar
class PullToRefreshActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linear_recycler_view)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.onItemClick { _, position ->
            Toast.makeText(applicationContext, "Click:" + position, Toast.LENGTH_SHORT).show()
        }
        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addHeaderView(getHeaderView())

        recyclerView.addFooterView(getHeaderView())
        recyclerView.addFooterView(getHeaderView())


        //初始设置2个,考虑其不满一屏加载状态
        val adapter = SimpleAdapter(this, Data.createItems(this, 2))
        recyclerView.setAdapter(adapter)
        recyclerView.onItemClick { v, position -> Snackbar.make(v, getString(R.string.click_position, position), Snackbar.LENGTH_LONG).show() }
        //下拉加载
        recyclerView.onRefresh {
            recyclerView.postDelayed({
                adapter.addItemsNotify(Data.createItems(this, 2), 0)
                recyclerView.onRefreshComplete()
            }, 1000)
        }
        //上拉刷新
        var times=0
        recyclerView.onFooterRefresh {
            if (times < 2) {
                recyclerView.postDelayed({
                    adapter.addItemsNotify(Data.createItems(this, 4))
                    recyclerView.onRefreshFootComplete()
                }, 1000)
            } else if (times < 4) {
                recyclerView.postDelayed({
                    recyclerView.onFootRetry {
                        adapter.addItemsNotify(Data.createItems(this, 4))
                        recyclerView.onRefreshFootComplete()
                    }
                }, 1000)
            } else {
                recyclerView.postDelayed({ recyclerView.setFooterRefreshDone() }, 1000)
            }
            times++
        }
        refreshModeLayout.setOnCheckedListener(object : RadioLayout.OnCheckedListener{
            override fun onChecked(v: View, position: Int, isChecked: Boolean) {
                recyclerView.setRefreshMode(when (position) {
                    0 ->  RefreshMode.BOTH
                    1 -> RefreshMode.PULL_START
                    2 -> RefreshMode.PULL_END
                    else -> RefreshMode.DISABLED
                })
            }

        })
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
        menuInflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            recyclerView.autoRefresh(true)
            return true
        } else if (id == R.id.action_re_refresh) {
            recyclerView.autoRefresh(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}
