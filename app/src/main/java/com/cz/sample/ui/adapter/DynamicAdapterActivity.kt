package com.cz.sample.ui.adapter

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.recyclerlibrary.adapter.dynamic.DynamicAdapter
import com.cz.recyclerlibrary.anim.SlideInLeftAnimator
import com.cz.recyclerlibrary.observe.DynamicAdapterDataObserve
import com.cz.recyclerlibrary.onItemClick
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_full_adapter.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

import java.util.LinkedList
import java.util.Random

/**
 * Created by cz on 16/1/24.
 */
@ToolBar
class DynamicAdapterActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_adapter)
        setTitle(intent.getStringExtra("title"))
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val simpleAdapter=SimpleAdapter(this, Data.createItems(this, 30))
        val random = Random()
        recyclerView.onItemClick { _, _,position -> Toast.makeText(this, "position:" + position, Toast.LENGTH_SHORT).show() }
        recyclerView.adapter = simpleAdapter
        //单独使用DynamicAdapter为
        // 1:创建一个DynamicAdapter并包装原Adapter
        // 2:并registerAdapterDataObserver DynamicAdapterDataObserve 传入dynamicAdapter
        // 3:RecyclerView.adapter=dynamicAdapter
//        val adapter = DynamicAdapter(simpleAdapter)
//        simpleAdapter.registerAdapterDataObserver(DynamicAdapterDataObserve(adapter))

        buttonAdd.onClick {
            val itemCount = simpleAdapter.itemCount
            simpleAdapter.addItemNotify("new:" + itemCount, if(0==itemCount) 0 else random.nextInt(itemCount))
        }
        buttonRemove.onClick {
            if (0 != simpleAdapter.itemCount) {
                simpleAdapter.removeItemNotify(0, Math.min(8,simpleAdapter.itemCount))
            } else {
                AlertDialog.Builder(this@DynamicAdapterActivity).
                        setTitle(R.string.add_random_item).
                        setNegativeButton(android.R.string.cancel,{dialog, _ -> dialog.dismiss() }).
                        setPositiveButton(android.R.string.ok,{_, _ ->
                            //添加30个动态条目
                            simpleAdapter.addItemsNotify(Data.createItems(this, 30))
                        }).show()
            }
        }
        buttonRandomAdd.onClick {
            val itemCount = simpleAdapter.itemCount
//            addView(adapter,if(0==itemCount) 0 else random.nextInt(itemCount))
            addView(13)
        }
        buttonRandomRemove.onClick {
            val dynamicItemCount = recyclerView.dynamicItemCount
            if(0==dynamicItemCount){
                toast(R.string.no_any_dynamic_item)
            } else {
                recyclerView.removeDynamicView(if(0==dynamicItemCount) 0 else random.nextInt(dynamicItemCount))
            }
        }

        //动态添加提示弹窗
        AlertDialog.Builder(this).
                setTitle(R.string.add_dynamic_item).
                setNegativeButton(android.R.string.cancel,{dialog, _ -> dialog.dismiss() }).
                setPositiveButton(android.R.string.ok,{_, _ ->
            //添加30个动态条目
            for(i in 0..10){
                addView(random.nextInt(recyclerView.itemCount))
            }
        }).show()
    }

    private fun addView(position: Int) =recyclerView.addDynamicView(getFullItemView(), position)

    /**
     * 获得一个铺满的控件
     */
    private fun getFullItemView(): View{
        val color = Data.randomColor
        val darkColor = Data.getDarkColor(color)
        val header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, findViewById(android.R.id.content) as ViewGroup, false)
        val headerView = header as TextView
        header.setBackgroundColor(color)
        headerView.setTextColor(darkColor)
        headerView.text = "DynamicView:" + recyclerView.dynamicItemCount
        return headerView
    }


}
