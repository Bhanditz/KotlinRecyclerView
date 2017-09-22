package com.cz.recyclerlibrary.layoutmanager.gallery

import android.content.Context
import android.support.annotation.FloatRange
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager

/**
 * Created by cz on 2017/9/22.
 */
class Gallery(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView(context, attrs, defStyle) {
    constructor(context: Context):this(context,null,0)
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    val layoutManager=CenterLinearLayoutManager(BaseLinearLayoutManager.HORIZONTAL)

    init {
        super.setLayoutManager(layoutManager)
        context.obtainStyledAttributes(attrs, R.styleable.Gallery).apply{
            setMinScrollOffset(getFloat(R.styleable.Gallery_gallery_minScrollOffset,0f))
            setCycleCount(getInteger(R.styleable.Gallery_gallery_minCycleCount,1))
            setCycle(getBoolean(R.styleable.Gallery_gallery_cycle,false))
            recycle()
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
    }

    /**
     * 设置最小的滑动偏移量
     */
    fun setMinScrollOffset(@FloatRange(from=0.0,to = 1.0) scrollOffset:Float){
        layoutManager.setMinScrollOffset(scrollOffset)
    }

    /**
     * 最小循环个数
     */
    fun setCycleCount(minCycleCount:Int){
        layoutManager.setCycleCount(minCycleCount)
    }

    /**
     * 设置是否循环
     */
    fun setCycle(cycle:Boolean){
        layoutManager.cycle=cycle
    }

    fun setOnSelectPositionChangedListener(listener: OnSelectPositionChangedListener) {
        layoutManager.setOnSelectPositionChangedListener(listener)
    }

    fun onSelectPositionChanged(callback:(View?, Int, Int)->Unit){
        setOnSelectPositionChangedListener(object :OnSelectPositionChangedListener{
            override fun onSelectPositionChanged(view: View?, position: Int, lastPosition: Int) {
                callback(view,position,lastPosition)
            }
        })
    }
}