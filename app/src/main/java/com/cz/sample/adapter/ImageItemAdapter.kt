package cz.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.cz.sample.R
import cz.widget.viewpager.BasePagerAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Administrator on 2017/6/3.
 */
class ImageItemAdapter(context:Context,items:List<String>) : BasePagerAdapter<String>(items) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    val colors= arrayOf(Color.GREEN,Color.BLUE,Color.GREEN,Color.YELLOW,Color.LTGRAY,Color.MAGENTA,Color.RED)
    override fun bindView(view: View,item:String, position: Int) {
        view.setBackgroundColor(colors[position])
        view.find<TextView>(R.id.tv_text).text=item
        view.onClick { Toast.makeText(view.context,"点击:$position",Toast.LENGTH_SHORT).show() }
    }

    override fun newView(container: ViewGroup, position: Int): View =layoutInflater.inflate(R.layout.pager_item, container, false)
}