package com.cz.sample.ui.adapter

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_header.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

/**
 * Created by cz on 16/1/23.
 * 此实现RecyclerView可支持动态添加HeaderView/FooterView,且实现以下以点
 * 1:无任何添加限制.不像ListView headerView必须在setAdapter前添加
 * 2:可添加不限制数量的header/footer,可动态移除
 * 3:采用装饰者模式设计,不影响用户本身的Adapter的逻辑
 */
@ToolBar
class HeaderAdapterActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_header)
        setTitle(intent.getStringExtra("title"))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.addDuration=300
        val adapter = SimpleAdapter(this, Data.createItems(this, 10))
        recyclerView.adapter=adapter
        recyclerView.addHeaderView(getHeaderView())
        recyclerView.addFooterView(getFooterView())
        buttonAddHeader.onClick {  recyclerView.addHeaderView(getHeaderView()) }
        buttonRemoveHeader.onClick {
            if(0==recyclerView.headerViewCount){
                toast("当前没有更多列表头!")
            } else {
                recyclerView.removeHeaderView(0)
            }
        }
        buttonAddFooter.onClick { recyclerView.addFooterView(getFooterView()) }
        buttonRemoveFooter.onClick {
            if(0==recyclerView.footerViewCount){
                toast("当前没有更多列表尾!")
            } else {
                recyclerView.removeFooterView(0)
            }
        }
        buttonAddItem.onClick {  adapter.addItemsNotify(Data.createItems(this, 2), 0) }
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
