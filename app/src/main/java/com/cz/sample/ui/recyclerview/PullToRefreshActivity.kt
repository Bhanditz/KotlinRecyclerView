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
import android.widget.Toast

import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.sample.widget.RadioLayout
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import com.cz.recyclerlibrary.onFootRetry
import com.cz.recyclerlibrary.onFooterRefresh
import com.cz.recyclerlibrary.onItemClick
import com.cz.recyclerlibrary.onRefresh
import com.cz.sample.R

import cz.refreshlayout.library.RefreshMode
import cz.refreshlayout.library.header.MaterialDesignHeader
import cz.refreshlayout.library.header.WalletHeader
import cz.refreshlayout.library.strategy.FollowStrategy
import cz.refreshlayout.library.strategy.FrontStrategy
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
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.onItemClick { _, _,position ->
            Toast.makeText(applicationContext, "Click:" + position, Toast.LENGTH_SHORT).show()
        }
        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addHeaderView(getHeaderView())

        recyclerView.addFooterView(getFooterView())
        recyclerView.addFooterView(getFooterView())


        //初始设置2个,考虑其不满一屏加载状态
        val adapter = SimpleAdapter(this, Data.createItems(this, 2))
        recyclerView.adapter=adapter
        recyclerView.onItemClick { v, _,position -> Snackbar.make(v, getString(R.string.click_position, position), Snackbar.LENGTH_LONG).show() }
        //下拉加载
        recyclerView.onRefresh {
            recyclerView.onRefreshComplete {
                adapter.addItemsNotify(Data.createItems(this, 2), 0)
            }
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

        //更新刷新头,以及模式
        refreshHeaderLayout.setOnCheckedListener(object : RadioLayout.OnCheckedListener{
            override fun onChecked(v: View, position: Int, isChecked: Boolean) {
                when (position) {
                    0 -> {
                        //设置刷新头为MaterialDesign样式,刷新策略为前置
                        recyclerView.setRefreshHeader(MaterialDesignHeader(v.context))
                        recyclerView.setHeaderStrategy(FrontStrategy())
                    }
                    1 -> {
                        //设置刷新头为钱包样式,刷新策略为刷新头追随
                        recyclerView.setRefreshHeader(WalletHeader(v.context))
                        recyclerView.setHeaderStrategy(FollowStrategy())
                    }
                }
            }
        })
        //更换刷新模式
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
        headerView.onClick { recyclerView.addHeaderView(getHeaderView()) }
        return headerView
    }

    private fun getFooterView(): View{
        val textColor = Data.randomColor
        val header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, findViewById(android.R.id.content) as ViewGroup, false)
        val headerView = header as TextView
        headerView.setTextColor(textColor)
        headerView.text = "FooterView:" + recyclerView.headerViewCount
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
