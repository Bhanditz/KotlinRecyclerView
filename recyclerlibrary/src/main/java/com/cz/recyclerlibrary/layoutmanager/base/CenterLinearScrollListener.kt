package com.cz.recyclerlibrary.layoutmanager.base

import android.support.v7.widget.RecyclerView
import com.cz.recyclerlibrary.debugLog
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener


/**
 * Created by cz on 17/1/16
 */
class CenterLinearScrollListener(private val layoutManager: CenterLinearLayoutManager) : RecyclerView.OnScrollListener() {
    private var listener: OnSelectPositionChangedListener?=null
    private var isSmoothScrolling: Boolean = false
    private var centerPosition=0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager
        if (!isSmoothScrolling && newState == RecyclerView.SCROLL_STATE_IDLE) {
            isSmoothScrolling = true
            if (layoutManager.canScrollHorizontally()) {
                recyclerView.smoothScrollBy(this.layoutManager.currentItemPositionOffset, 0)
            } else if (layoutManager.canScrollVertically()) {
                recyclerView.smoothScrollBy(0, this.layoutManager.currentItemPositionOffset)
            }
        } else if (layoutManager.isSmoothScrolling && newState == RecyclerView.SCROLL_STATE_SETTLING) {
            //静止状态执行smoothScroll,此段代码非常重要,因为smoothScroll会触发onScrollStateChanged,SCROLL_STATE_IDLE触发.会最终使smoothScroll滑动位置计算错误
            isSmoothScrolling = true
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            isSmoothScrolling = false
        }
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val currentCenterPosition=layoutManager.findCurrentItemPosition()
        if(centerPosition!=currentCenterPosition){
            val centerView=layoutManager.findViewByPosition(currentCenterPosition)
            listener?.onSelectPositionChanged(centerView,currentCenterPosition,centerPosition)
            centerPosition=currentCenterPosition
        }
    }

    fun setOnSelectPositionChangedListener(listener: OnSelectPositionChangedListener) {
        this.listener = listener
    }
}
