package cz.kotlinwidget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cz.sample.R
import kotlinx.android.synthetic.main.activity_pager_list.*

/**
 * Created by Administrator on 2017/6/12.
 */
class NewViewPagerListActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_list)
        title=intent.getStringExtra("title")
        toolBar.title = title
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener{ finish() }

        if(null==savedInstanceState){
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, NewPagerListFragment()).commit()
        }
    }
}