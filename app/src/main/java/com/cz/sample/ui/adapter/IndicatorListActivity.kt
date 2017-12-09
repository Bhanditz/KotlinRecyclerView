package com.cz.sample.ui.adapter

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.cz.sample.R
import com.cz.sample.adapter.LinearSticky1ItemAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_list_indicator.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar
class IndicatorListActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_indicator)
        setTitle(intent.getStringExtra("title"))
        refreshRecyclerView.layoutManager= LinearLayoutManager(this)
        val adapter=LinearSticky1ItemAdapter(this, Data.ITEMS.toList())
        refreshRecyclerView.adapter=adapter
        refreshRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition = refreshRecyclerView.firstVisiblePosition
                val startIndex = adapter.strategy.getStartIndex(firstVisiblePosition)
                val item = adapter.getItem(adapter.strategy.getOriginalIndex(startIndex))
                textView.text=item[0].toString()
                textView.visibility= View.VISIBLE
                listIndicator.setSelectPosition(startIndex)
                //更新位置
                val text=firstVisiblePosition.toString()
                editor.setText(text)
                editor.setSelection(text.length)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                textView.visibility=if(RecyclerView.SCROLL_STATE_IDLE==newState) View.GONE else View.VISIBLE
            }
        })
        listIndicator.setOnIndicatorListener { _, i ->
            refreshRecyclerView.scrollToPosition(adapter.strategy.getOriginalIndex(i))
        }
        listIndicator.setOnIndicatorCancelListener { textView.visibility=View.GONE }
        listIndicator.setIndicatorItems(adapter.indicateItems)
        scrollButton.onClick {
            val position=editor.text.toString().toInt()
            refreshRecyclerView.smoothScrollToPosition(position)
        }
    }
}
