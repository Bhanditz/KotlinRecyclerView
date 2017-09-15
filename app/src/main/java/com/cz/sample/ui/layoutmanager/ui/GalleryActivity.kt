package com.cz.sample.ui.layoutmanager.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView

import com.cz.recyclerlibrary.layoutmanager.gallery.GalleryLayoutManager
import com.cz.sample.R
import com.cz.sample.annotation.ToolBar
import com.cz.sample.ui.layoutmanager.GalleryImageAdapter
import cz.volunteerunion.ui.ToolBarActivity

import java.util.ArrayList

/**
 * Created by cz on 1/18/17.
 */
@ToolBar
class GalleryActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setTitle(intent.getStringExtra("title"))
        val recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val items = ArrayList<Int>()
        items.add(R.mipmap.gallery_1)
        items.add(R.mipmap.gallery_2)
        items.add(R.mipmap.gallery_3)
        items.add(R.mipmap.gallery_4)
        items.add(R.mipmap.gallery_5)
        items.add(R.mipmap.gallery_6)
        items.add(R.mipmap.gallery_7)
        items.add(R.mipmap.gallery_8)
        items.add(R.mipmap.gallery_9)
        items.add(R.mipmap.gallery_10)
        items.add(R.mipmap.gallery_11)

        val galleryLayoutManager = GalleryLayoutManager()
        galleryLayoutManager.setMinScrollOffset(0.6f)
        galleryLayoutManager.setGravity(GalleryLayoutManager.START)
        recyclerView.layoutManager = galleryLayoutManager
        recyclerView.adapter = GalleryImageAdapter(this, items)
    }
}
