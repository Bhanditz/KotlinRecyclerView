package com.ldzs.pulltorefreshrecyclerview.ui.sticky

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import com.ldzs.pulltorefreshrecyclerview.R
import com.ldzs.pulltorefreshrecyclerview.adapter.LinearSticky2ItemAdapter
import com.ldzs.pulltorefreshrecyclerview.annotation.ToolBar
import com.ldzs.pulltorefreshrecyclerview.data.Data
import com.ldzs.pulltorefreshrecyclerview.model.Sticky1Item
import com.ldzs.recyclerlibrary.PullToRefreshStickyRecyclerView
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_sticky2.*

import java.util.ArrayList
import java.util.Random

/**
 * Created by Administrator on 2017/5/20.
 * 此示例演示Sticky在adapter内为一个单独的type,分组内型为组内单独条目,且header支持动态大小.
 * 分组逻辑为:(Sticky1Item s1)->!s1.headerItems.isEmpty()
 * 因动态大小加入,所以探测改为:遍历当前列表内是否有待出现下个阶段条目
 */
@ToolBar
class Sticky2SampleActivity : ToolBarActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky2)
        setTitle(intent.getStringExtra("title"))
        refreshStickyRecyclerView.layoutManager = LinearLayoutManager(this)
        val items = ArrayList<Sticky1Item>()
        var lastItem: String? = null
        val random = Random()
        for (item in Data.ITEMS) {
            val word = item[0].toString()
            if (null == lastItem || lastItem != word) {
                items.add(Sticky1Item(getStickyItems(word, 4 + random.nextInt(4)), word))
            } else {
                items.add(Sticky1Item(item))
            }
            lastItem = word
        }
        refreshStickyRecyclerView.setAdapter(LinearSticky2ItemAdapter(this, items))
    }

    private fun getStickyItems(item: String, n: Int): Array<String> {
        val list = mutableListOf<String>()
        for (i in 0..n - 1) {
            list+=item.toUpperCase() + (i + 1)
        }
        return list.toTypedArray()
    }
}
