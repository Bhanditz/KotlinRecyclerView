package com.ldzs.pulltorefreshrecyclerview.ui.sticky

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky1ItemAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_sticky1.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import java.util.ArrayList
import java.util.Arrays
import java.util.Random

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示,自动分组,分组条目为不同条目区间运算
 * 分组逻辑为:(String s1, String s2)->s1.charAt(0)!=s2.charAt(0)
 */
@ToolBar
class Sticky1SampleActivity : ToolBarActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky1)
        setTitle(intent.getStringExtra("title"))
        refreshStickyRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LinearSticky1ItemAdapter(this, Arrays.asList(*Data.ITEMS))
        refreshStickyRecyclerView.setAdapter(adapter)

        var swap=false
        buttonWrap.onClick {
            swap = !swap
            if (swap) {
                val random = Random()
                val items = mutableListOf<String>()
                var i = 'H'
                while (i <= 'Z') {
                    for (k in 1..5 + random.nextInt(5)) {
                        items.add(i.toString() + " Item:" + k)
                    }
                    i++
                }
                adapter.swapItemsNotify(items)
            } else {
                adapter.swapItemsNotify(Arrays.asList(*Data.ITEMS))
            }
        }
    }
}
