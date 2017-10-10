package com.cz.sample.ui.layoutmanager

import android.os.Bundle

import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.data.Data
import com.cz.sample.ui.layoutmanager.adapter.TableItemAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_table.*
import org.jetbrains.anko.toast

import java.util.Arrays

/**
 * Created by cz on 2017/1/21.
 */
@ToolBar
class TableActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        setTitle(intent.getStringExtra("title"))
        tableView.adapter = TableItemAdapter(this, Arrays.asList(*Data.ITEMS))
        tableView.onItemClick { _, i ->
            toast("Click:$i")
        }
        tableView.onItemLongClick { _, i ->
            toast("Long Click:$i")
            false
        }
    }
}
