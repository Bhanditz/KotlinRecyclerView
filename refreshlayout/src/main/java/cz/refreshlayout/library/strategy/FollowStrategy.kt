package cz.refreshlayout.library.strategy

import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import cz.refreshlayout.library.PullToRefreshLayout
import cz.refreshlayout.library.RefreshState
import cz.refreshlayout.library.debugLog
import cz.refreshlayout.library.header.BaseRefreshHeader
import java.util.*

/**
 * Created by cz on 2017/7/28.
 * 一个刷新头跟随列表的滚动策略
 */
class FollowStrategy(layout: PullToRefreshLayout) : BaseStrategy(layout) {
    private val flingAction = FlingAction()

    override fun onAddRefreshView(header: View, refreshView: View?) {
        layout.addView(header, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.addView(refreshView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onRefreshViewLayout(header: View, refreshView: View?, left: Int, top: Int, right: Int, bottom: Int) {
        //header排版
        header.layout((layout.measuredWidth-header.measuredWidth)/2,
                -header.measuredHeight,(layout.measuredWidth+header.measuredWidth)/2,0)
        //内容排版
        refreshView?.layout(layout.paddingLeft, layout.paddingTop,
                layout.paddingLeft+refreshView.measuredWidth,
                layout.paddingTop+refreshView.measuredHeight)
    }

    override fun onRefreshScroll(target: View, refreshHeader: BaseRefreshHeader, refreshHeight:Int, dx: Int, dy: Int, maxScroll:Int, consumed: IntArray) {
        var dy=dy
        //向上防止越界
        if(0>dy){
            //往上滑
            if(-maxScroll>layout.scrollY){
                dy= 0
            } else if(-maxScroll>layout.scrollY+dy){
                dy=-layout.scrollY-maxScroll
            }
        }
        debugLog("onRefreshScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} ${layout.scrollY} $maxScroll")
        if(0>dy&&!ViewCompat.canScrollVertically(target,dy)){
            layout.scrollBy(0,dy)
            consumed[1]=dy
            onRefreshScrollChanged(refreshHeader,refreshHeight)
        } else if(layout.scrollY<target.top){
            if(dy+layout.scrollY>target.top){
                dy=target.top-layout.scrollY
            }
            consumed[1]=dy
            layout.scrollBy(0,dy)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
            debugLog("onNestedPreScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} ${layout.scrollY}")
        }
    }

    override fun onRefreshPreFling(target: View, velocityX: Float, velocityY: Float,refreshHeight:Int): Boolean {
        debugLog("onRefreshPreFling: top:${target.top} ${layout.scrollY}")
//        if(layout.scrollY<target.top){
//            if(-layout.scrollY>=refreshHeight){
//                layout.startScroll(0,layout.scrollY,0,-refreshHeight-layout.scrollY)
//                setRefreshing()
//            } else {
//                layout.startScroll(0,layout.scrollY,0,-layout.scrollY)
//                layout.setRefreshState(RefreshState.NONE)
//            }
//        }
        return layout.scrollY<target.top
    }

    override fun onStopRefreshScroll(target: View,refreshHeight:Int) {
        debugLog("onStopRefreshScroll: scrollY:${layout.scrollY} $refreshHeight")
        if(!layout.isRefreshState(RefreshState.REFRESHING_COMPLETE)&&-layout.scrollY>=refreshHeight){
            //如果已经为刷新完毕状态,直接还原列表
            layout.startScroll(0,layout.scrollY,0,-refreshHeight-layout.scrollY)
            if(!layout.isRefreshing()){
                setRefreshing()
            }
        } else {
            layout.startScroll(0,layout.scrollY,0,-layout.scrollY)
            //此处尽量与动画保持一致
            layout.setRefreshState(RefreshState.NONE)
        }
    }

    private fun onRefreshScrollChanged(refreshHeader: BaseRefreshHeader,refreshHeight:Int,autoRefresh: Boolean=false){
        if(autoRefresh||!layout.isRefreshing()){
            val fraction=Math.min(1f,Math.abs(layout.scrollY)*1f/refreshHeight)
            refreshHeader.onScrollOffset(fraction)
            layout.setRefreshState(if(1f>=fraction) RefreshState.RELEASE_TO_REFRESHING else RefreshState.RELEASE_TO_CANCEL)
        }
    }

    override fun autoRefresh(refreshHeight:Int, smooth: Boolean) {
        if(smooth){
            layout.startScroll(0,layout.scrollY,0,-refreshHeight-layout.scrollY)
            //滑动结束后,将当前刷新状态置为刷新中
            flingAction.finishedAction={ setRefreshing() }
            layout.removeCallbacks(flingAction)
            layout.post(flingAction)
        } else {
            layout.scrollTo(0,-refreshHeight-layout.scrollY)
            setRefreshing()
        }
        layout.invalidate()
    }

    override fun onRefreshComplete(action: (() -> Unit)?) {
        layout.startScroll(0,layout.scrollY,0,-layout.scrollY,600)
        layout.setRefreshState(RefreshState.NONE)
        action?.invoke()
    }

    /**
     * 惯性滑动事件
     */
    inner class FlingAction :Runnable{
        var finishedAction:(()->Unit)?=null
        override fun run() {
            val scroller = layout.getRefreshScroller()
            if(!scroller.isFinished&&scroller.computeScrollOffset()){
                onRefreshScrollChanged(layout.getRefreshHeader(),layout.refreshHeight,true)
                layout.postDelayed(this,16)
            } else {
                finishedAction?.invoke()
            }
        }

    }


}