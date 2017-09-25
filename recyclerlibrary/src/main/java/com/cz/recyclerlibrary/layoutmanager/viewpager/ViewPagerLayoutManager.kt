package com.cz.recyclerlibrary.layoutmanager.viewpager

import android.content.Context
import android.support.annotation.FloatRange
import android.view.ViewConfiguration
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager

/**
 * Created by cz on 2017/9/25.
 * 一个ViewPager的LayoutManager
 */
class ViewPagerLayoutManager(context: Context, orientation: Int) : CenterLinearLayoutManager(orientation) {
    val scaledMinimumFlingVelocity= ViewConfiguration.get(context).scaledMinimumFlingVelocity

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

    }
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
            debugLog("onLayoutFling:$targetIndex")
            val view=getChildAt(targetIndex)?:getChildAt(centerIndex)
            smoothScrollToView(recyclerView,view)
        }
        return true
    }

}