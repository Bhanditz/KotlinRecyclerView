package com.ldzs.pulltorefreshrecyclerview.ui.sticky

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky1ItemAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.onItemClick
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView
import com.ldzs.recyclerlibrary.callback.OnItemClickListener
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_sticky3.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import java.util.ArrayList
import java.util.Random

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示,自动分组,分组后不同运态增删数据的分组自动同步功能
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
@ToolBar
class Sticky3SampleActivity : ToolBarActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky3)
        setTitle(intent.getStringExtra("title"))
        val items = ArrayList<String>()
        var i = 'A'
        while (i < 'N') {
            for (k in 0..4) {
                items.add(i.toString() + " index:" + (k + 1))
            }
            i++
        }
        refreshStickyRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LinearSticky1ItemAdapter(this, items)
        refreshStickyRecyclerView.setAdapter(adapter)
        refreshStickyRecyclerView.onItemClick { _, position -> Toast.makeText(this, "position:" + position, Toast.LENGTH_SHORT).show() }

        val random = Random()
        buttonRemove.onClick{ adapter.removeNotify(random.nextInt(adapter.itemCount)) }
    }
}
