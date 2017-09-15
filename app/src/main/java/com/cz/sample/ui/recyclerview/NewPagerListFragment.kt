package cz.kotlinwidget.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.onRefresh
import com.cz.sample.R
import com.cz.sample.adapter.SimpleAdapter
import com.cz.sample.data.Data
import cz.myapplication.ImageItemAdapter
import cz.widget.viewpager.NewViewPager
import kotlinx.android.synthetic.main.fragment_view_pager_list.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.find

/**
 * Created by Administrator on 2017/7/2.
 */
class NewPagerListFragment :Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =inflater.inflate(R.layout.fragment_view_pager_list,container,false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.layoutManager= LinearLayoutManager(context)
        val adapter = SimpleAdapter(context, Data.ITEMS)
        recyclerView.adapter=adapter
        val viewPager = recyclerView.findAdapterView<NewViewPager>(R.id.viewPager)
        val itemAdapter = ImageItemAdapter(context, listOf("Item1", "Item2", "Item3"))
        viewPager?.setAdapter(itemAdapter)
        viewPager?.startAutoScroll()
        addItem.onClick {
            itemAdapter.items.add("Item${itemAdapter.count+1}")
            itemAdapter.notifyDataSetChanged()
        }
        removeItem.onClick {
            if(1<itemAdapter.count){
                itemAdapter.items.removeAt(itemAdapter.count-1)
                itemAdapter.notifyDataSetChanged()
            }
        }

        recyclerView.onRefresh {
            recyclerView.onRefreshComplete { adapter.addItemNotify("NewItem",0) }
        }
    }
}