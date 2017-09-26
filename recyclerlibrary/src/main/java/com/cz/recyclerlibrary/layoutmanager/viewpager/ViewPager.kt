package com.cz.recyclerlibrary.layoutmanager.viewpager

import android.content.Context
import android.support.annotation.FloatRange
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager

/**
 * Created by cz on 2017/9/25.
 */
class ViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyle: Int=0) : RecyclerView(context, attrs, defStyle) {
    companion object {
        const val HORIZONTAL = OrientationHelper.HORIZONTAL
        const val VERTICAL = OrientationHelper.VERTICAL
    }
    private val layoutManager=ViewPagerLayoutManager(context,BaseLinearLayoutManager.HORIZONTAL)
    init {
        context.obtainStyledAttributes(attrs, R.styleable.ViewPager).apply{
            setItemSizeFactor(getFloat(R.styleable.ViewPager_vp_itemSizeFactor,1f))
        }
    }

    fun setOrientation(orientation:Int){
        this.layoutManager.orientation=orientation
    }

    /**
     * 条目显示
     */
    private fun setItemSizeFactor(factor: Float) {
        this.layoutManager.setItemSizeFactor(factor)
    }

    /**
     * @param layout
     */
    @Deprecated("nothing to do", ReplaceWith("Unit"))
    override fun setLayoutManager(layout: RecyclerView.LayoutManager)=Unit
    /**
     * @param decor
     *
     */
    @Deprecated("nothing to do")
    override fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
    }

    /**
     * 设置最小的滑动偏移量
     */
    fun setMinScrollOffset(@FloatRange(from=0.0,to = 1.0) scrollOffset:Float){
        layoutManager.setMinScrollOffset(scrollOffset)
    }

    override fun setAdapter(adapter: Adapter<RecyclerView.ViewHolder>?) {
        super.setLayoutManager(layoutManager)
        super.setAdapter(adapter)
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
        setOnSelectPositionChangedListener(object : OnSelectPositionChangedListener {
            override fun onSelectPositionChanged(view: View?, position: Int, lastPosition: Int) {
                callback(view,position,lastPosition)
            }
        })
    }
}