package com.cz.recyclerlibrary.layoutmanager.viewpager

import android.content.Context
import android.support.annotation.FloatRange
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewConfiguration
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager

/**
 * Created by cz on 2017/9/25.
 * 一个ViewPager的LayoutManager
 */
class ViewPagerLayoutManager(context: Context, orientation: Int) : CenterLinearLayoutManager(orientation) {
    private val scaledMinimumFlingVelocity= ViewConfiguration.get(context).scaledMinimumFlingVelocity
    private var itemSizeFactor=1f

    override fun onLayoutFling(velocityX: Int, velocityY: Int): Boolean {
        val centerChildView = findOneVisibleChild(0, childCount)
        if(null!=centerChildView){
            val centerIndex=recyclerView.indexOfChild(centerChildView)
            var targetIndex=centerIndex
            if(HORIZONTAL==orientation&&Math.abs(velocityX)>scaledMinimumFlingVelocity){
                if(0<velocityX){
                    targetIndex=centerIndex+1
                } else {
                    targetIndex=centerIndex-1
                }
            } else if(VERTICAL==orientation&&Math.abs(velocityY)>scaledMinimumFlingVelocity){
                if(0<velocityY){
                    targetIndex=centerIndex+1
                } else {
                    targetIndex=centerIndex-1
                }
            }
            val view=getChildAt(targetIndex)?:getChildAt(centerIndex)
            smoothScrollToView(recyclerView,view)
        }
        return true
    }

    /**
     * 条目显示
     */
    fun setItemSizeFactor(factor: Float) {
        this.itemSizeFactor=factor
    }

    /**
     * 复写measureChildWithMargins,实现,在测试控件时,动态控制控件尺寸比例
     */
    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        val sizeUsed=orientationHelper.end-orientationHelper.end*itemSizeFactor
        if(HORIZONTAL==orientation){
            super.measureChildWithMargins(child, sizeUsed.toInt(), heightUsed)
        } else {
            super.measureChildWithMargins(child, widthUsed, sizeUsed.toInt())
        }
    }



}