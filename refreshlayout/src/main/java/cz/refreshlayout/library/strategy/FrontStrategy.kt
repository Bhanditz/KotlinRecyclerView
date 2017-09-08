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
 * 一个刷新头前置策略,用于仿系统的MaterialDesign效果策略
 */
class FrontStrategy(layout: PullToRefreshLayout) : BaseStrategy(layout) {

    override fun onAddRefreshView(header: View, refreshView: View?) {
        //此处将header排在refreshView后面
        layout.addView(refreshView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.addView(header, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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

    override fun onRefreshScroll(target: View,refreshHeader: BaseRefreshHeader,refreshHeight:Int, dx: Int, dy: Int,maxScroll:Int, consumed: IntArray) {
        var dy=dy
        val headerView = layout.getRefreshHeaderView()
        //向上防止越界
        if(0>dy){
            //往上滑
            if(maxScroll<headerView.bottom){
                dy= 0
            } else if(maxScroll<dy+headerView.bottom){
                dy=maxScroll-headerView.bottom
            }
        }
        debugLog("onRefreshScroll:${Arrays.toString(consumed)} dy:$dy bottom:${headerView.bottom} ${layout.scrollY} $maxScroll ${layout.getRefreshState()}")
        if(!layout.isRefreshing()&&
                (0>dy&&!ViewCompat.canScrollVertically(target,dy)||headerView.bottom>target.top)){
            consumed[1]=dy
            headerView.offsetTopAndBottom(-dy)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
        }
    }


    override fun onRefreshPreFling(target: View, velocityX: Float, velocityY: Float,refreshHeight:Int): Boolean {
//        val headerView = layout.getRefreshHeaderView()
//        debugLog("onRefreshPreFling: top:${target.top} ${headerView.bottom}")
//        //这里以模式进行约束,因为,Front模式列表位置一直固定
//        if(headerView.bottom>target.top&& !layout.isRefreshing()){
//            if(0>velocityY||refreshHeight<headerView.bottom){
//                debugLog("startScroll>: velocityY:$velocityY top:${headerView.bottom} ${headerView.height}")
//                setRefreshing()
//                offsetHeaderTopAndBottom(headerView,headerView.bottom-refreshHeight)
//            } else {
//                offsetHeaderTopAndBottom(headerView,headerView.bottom){ layout.setRefreshState(RefreshState.NONE) }
//                debugLog("startScroll<: velocityY:$velocityY top:${headerView.bottom} ${headerView.height}")
//            }
//        }
        //让列表在非刷新模式下一直可以滚动
        return false
    }
    override fun onStopRefreshScroll(target: View,refreshHeight:Int) {
        val headerView = layout.getRefreshHeaderView()
        //非刷新状态操作
        if(!layout.isRefreshing()){
            if(headerView.top>refreshHeight/2){
                //直接定义为刷新状态,避免中途被多次操作,引起定位异常
                setRefreshing()
                offsetHeaderTopAndBottom(headerView,headerView.bottom-refreshHeight)
            } else {
                offsetHeaderTopAndBottom(headerView,headerView.bottom){ layout.setRefreshState(RefreshState.NONE) }
            }
        } else if(layout.isRefreshState(RefreshState.REFRESHING_COMPLETE)){
            offsetHeaderTopAndBottom(headerView,headerView.bottom){ layout.setRefreshState(RefreshState.NONE) }
        }
        debugLog("onStopRefreshScroll: top:${headerView.top} ${headerView.height} ${layout.getRefreshState()}")
    }

    override fun autoRefresh(refreshHeight:Int, smooth: Boolean) {
        val refreshHeader = layout.getRefreshHeader()
        val headerView = layout.getRefreshHeaderView()
        if(smooth){
            offsetHeaderTopAndBottom(headerView,headerView.bottom-refreshHeight, {
                onRefreshScrollChanged(refreshHeader,refreshHeight) }){ setRefreshing() }
        } else {
            headerView.offsetTopAndBottom(headerView.bottom-refreshHeight)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
            layout.invalidate()
            setRefreshing()
        }
    }


    override fun onRefreshComplete(action: (() -> Unit)?) {
        val headerView = layout.getRefreshHeaderView()
        debugLog("onRefreshComplete:${layout.getRefreshState()}")
        offsetHeaderTopAndBottom(headerView,headerView.bottom){
            layout.setRefreshState(RefreshState.NONE)
            action?.invoke()
        }
    }

    /**
     * 滑动变化回调
     */
    private fun onRefreshScrollChanged(refreshHeader: BaseRefreshHeader,refreshHeight:Int){
        if(!layout.isRefreshing()){
            val headerView = layout.getRefreshHeaderView()
            val fraction=Math.min(1f,Math.abs(headerView.bottom)*1f/refreshHeight)
            refreshHeader.onScrollOffset(fraction)
            layout.setRefreshState(if(1f>=fraction) RefreshState.RELEASE_TO_REFRESHING else RefreshState.RELEASE_TO_CANCEL)
            debugLog("onRefreshScrollChanged: status:${layout.getRefreshState()} fraction:$fraction")
        }
    }

}
