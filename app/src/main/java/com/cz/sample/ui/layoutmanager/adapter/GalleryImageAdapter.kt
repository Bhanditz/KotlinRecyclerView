package com.cz.sample.ui.layoutmanager.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cz.recyclerlibrary.adapter.BaseViewHolder

import com.cz.recyclerlibrary.layoutmanager.base.ViewScrollOffsetCallback
import com.cz.sample.R

import java.util.ArrayList

/**
 * Created by cz on 1/18/17.
 */
class GalleryImageAdapter(context: Context, imageItems: List<Int>?) : RecyclerView.Adapter<BaseViewHolder>(), ViewScrollOffsetCallback {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val imageItems: MutableList<Int>

    init {
        this.imageItems = ArrayList<Int>()
        if (null != imageItems) {
            this.imageItems.addAll(imageItems)
        }
    }

    override fun onViewScrollOffset(view: View, position: Int, centerPosition: Int, offset: Float,minOffset:Float) {
        view.scaleX = 0.2f + Math.abs(minOffset)
        view.scaleY = 0.2f + Math.abs(minOffset)
//        view.rotation=360*Math.abs(minOffset)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Log.e(TAG, "onCreateViewHolder")
        return BaseViewHolder(layoutInflater.inflate(R.layout.gallery_image_item, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder:" + position)
        val imageView = holder.itemView.findViewById(R.id.iv_image) as ImageView
        val textView = holder.itemView.findViewById(R.id.tv_text) as TextView
        imageView.setImageResource(this.imageItems[position])
        textView.text = "Position:" + position
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
//        Log.e(TAG, "onViewRecycled:" + holder!!.adapterPosition)
    }

    override fun getItemCount(): Int {
        return this.imageItems.size
    }

    companion object {
        private val TAG = "GalleryImageAdapter"
    }
}
