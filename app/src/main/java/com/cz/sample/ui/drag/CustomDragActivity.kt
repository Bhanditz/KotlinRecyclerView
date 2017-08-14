package com.cz.sample.ui.drag

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.cz.sample.R
import com.cz.sample.adapter.ChannelAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.model.Channel
import com.cz.sample.util.JsonParser
import com.cz.recyclerlibrary.anim.FadeInDownAnimator
import com.cz.recyclerlibrary.onDragItemEnable
import com.cz.recyclerlibrary.onItemClick
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_costom_drag.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

import java.io.InputStreamReader

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by cz on 16/1/27.
 */
@ToolBar
class CustomDragActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costom_drag)
        setTitle(intent.getStringExtra("title"))
        //初始化数据
        Observable.create<List<Channel>> {
            val value=getContentFromAssets(resources, "item.json")
            val items= JsonParser.getLists(value, Channel::class.java)
            it.onNext(items)
            it.onCompleted()
        }.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(this::initAdapter)

    }

    private fun initAdapter(items:List<Channel>){
        recyclerView.itemAnimator = FadeInDownAnimator()
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ChannelAdapter(this, items)
        recyclerView.adapter = adapter
        val view = View.inflate(this, R.layout.recyclerview_header3, null)
        val editView = view.find<TextView>(R.id.tv_edit)
        var editMode=false
        editView.onClick {
            editMode = !editMode
            adapter.setDragStatus(editMode)
            editView.setText(if (editMode) R.string.complete else R.string.channel_sort_delete)
        }
        recyclerView.onItemClick { _, position ->
            val itemPosition = recyclerView.getItemPosition(position)//获得当前条目的位置
            val count = items.count { it.use }
            val item = adapter.getItem(itemPosition)
            if (itemPosition < count) {
                if (editMode) {
                    item?.use = false
                    recyclerView.setItemMove(position, count + 1)
                } else {
                    Toast.makeText(applicationContext, "Click:" + item?.name!!, Toast.LENGTH_SHORT).show()
                }
            } else {
                item?.use = true
                recyclerView.setItemMove(position, count + 1)
            }
        }
        recyclerView.addDynamicView(view, 0)
        recyclerView.addDynamicView(R.layout.recyclerview_header4, items.count { it.use } + 1)
        recyclerView.onDragItemEnable { position->
            val item=adapter.getItem(position)
            editMode && item?.use?:false
        }

    }

    /**
     * 从asset内读取文件内容
     * @param resource
     * *
     * @param fileName
     * *
     * @return
     */
    fun getContentFromAssets(resource: Resources, fileName: String): String {
        var result = StringBuilder()
        var inputStream: InputStreamReader? = null
        try {
            InputStreamReader(resource.assets.open(fileName)).apply { forEachLine { result.append(it) } }
        } finally {
            inputStream?.close()
        }
        return result.toString()
    }


}
