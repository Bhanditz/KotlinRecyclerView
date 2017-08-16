package com.cz.sample.ui.adapter

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.cz.sample.adapter.SimpleSelectAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.sample.widget.RadioLayout
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import com.cz.recyclerlibrary.onItemClick
import com.cz.recyclerlibrary.onMultiSelect
import com.cz.recyclerlibrary.onRectangleSelect
import com.cz.recyclerlibrary.onSingleSelect
import com.cz.sample.R
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_select.*
import org.jetbrains.anko.sdk25.coroutines.onClick


/**
 * Created by cz on 16/1/23.
 * 此实现RecyclerView自带的点选事件
 * 1:单击/单选/多选/块选
 */
@ToolBar
class SelectAdapterActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        setTitle(intent.getStringExtra("title"))
        recyclerView.itemAnimator = SlideInLeftAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)


        recyclerView.adapter=SimpleSelectAdapter(this, Data.createItems(this, 100))
        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addFooterView(getFooterView())
        choiceLayout.setOnCheckedListener(object : RadioLayout.OnCheckedListener {
            override fun onChecked(v: View, position: Int, isChecked: Boolean) {
                recyclerView.setSelectMode(position)
            }
        })
        recyclerView.onItemClick {_, _,position -> Toast.makeText(applicationContext, "Click:" + position, Toast.LENGTH_SHORT).show() }
        recyclerView.onSingleSelect { _, newPosition, _ ->
            Toast.makeText(applicationContext, "SingleSelect:" + newPosition, Toast.LENGTH_SHORT).show()
        }
        recyclerView.onMultiSelect { _, selectPositions, lastSelectCount, maxCount ->
            if (lastSelectCount < maxCount) {
                Toast.makeText(applicationContext, "MultiSelect:" + selectPositions, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "MultiSelect no thing", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.onRectangleSelect { start, end ->
            Toast.makeText(applicationContext, "Start:$start End:$end", Toast.LENGTH_SHORT).show()
        }
        recyclerView.findAdapterView(R.id.tv_last)?.onClick { Toast.makeText(this@SelectAdapterActivity, "List Item", Toast.LENGTH_SHORT).show() }
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
     * 获得一个底部控件
     */
    private fun getFooterView(): View{
        val color = Data.randomColor
        val textColor = Data.getDarkColor(color)
        val footer = LayoutInflater.from(this).inflate(R.layout.recyclerview_footer, findViewById(android.R.id.content) as ViewGroup, false)
        val footerView = footer as TextView
        footerView.text = "FooterView:" + recyclerView.footerViewCount
        footerView.setBackgroundColor(color)
        footerView.setTextColor(textColor)
        return footerView
    }
}
