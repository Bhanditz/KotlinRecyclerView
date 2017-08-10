package com.ldzs.pulltorefreshrecyclerview

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ldzs.pulltorefreshrecyclerview.model.SampleItem

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val id=intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")
        if(null==title) {
            toolBar.setTitle(R.string.app_name)
            setSupportActionBar(toolBar)
        } else {
            toolBar.title = title
            toolBar.subtitle=intent.getStringExtra("desc")
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolBar.setNavigationOnClickListener{ finish() }
        }
        recyclerView.layoutManager= LinearLayoutManager(this)
        val items = FuncTemplate[id]
        items?.let { recyclerView.setAdapter(Adapter(it)) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_about) {
            val uri = Uri.parse("https://github.com/momodae")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class Adapter(val items:List<SampleItem<Activity>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view=with(parent.context) {
                linearLayout {
                    orientation= LinearLayout.VERTICAL
                    lparams(width = matchParent, height = wrapContent)
                    leftPadding=dip(12)
                    rightPadding=dip(12)
                    backgroundResource=R.drawable.white_item_selector
                    textView {
                        id = android.R.id.text1
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                        topPadding = dip(4)
                        bottomPadding=dip(4)
                    }
                    textView {
                        id = android.R.id.text2
                        textSize = 14f
                        topPadding = dip(4)
                        bottomPadding=dip(4)
                        lines=2
                    }
                }
            }
            return object: RecyclerView.ViewHolder(view){}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item=items[position]
            holder.itemView.find<TextView>(android.R.id.text1).text=item.title
            holder.itemView.find<TextView>(android.R.id.text2).text=item.desc
            holder.itemView.onClick {
                val context=it?.context?:return@onClick
                if(item.id in FuncTemplate){
                    context.startActivity(Intent(context,MainActivity::class.java).apply {
                        putExtra("id",item.id)
                        putExtra("title",item.title)
                        putExtra("desc",item.desc)
                    })//子分组
                } else {
                    context.startActivity(Intent(context,item.clazz).apply { putExtra("title",item.title) })//子条目
                }
            }
        }

        override fun getItemCount(): Int=items.size


    }

}
