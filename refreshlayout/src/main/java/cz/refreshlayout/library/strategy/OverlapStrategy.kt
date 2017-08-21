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
 * 一个刷新头藏在列表背后的策略,仿早期网页新闻刷新策略
 */
class OverlapStrategy(layout: PullToRefreshLayout) : BaseStrategy(layout) {

    override fun onAddRefreshView(header: View, refreshView: View?) {
        //此处将header排在refreshView后面
        layout.addView(header, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout.addView(refreshView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    override fun onRefreshViewLayout(header: View, refreshView: View?, left: Int, top: Int, right: Int, bottom: Int) {
        //header排版
        header.layout((layout.measuredWidth-header.measuredWidth)/2,
                0,(layout.measuredWidth+header.measuredWidth)/2,header.measuredHeight)
        //内容排版
        refreshView?.layout(layout.paddingLeft, layout.paddingTop,
                layout.paddingLeft+refreshView.measuredWidth,
                layout.paddingTop+refreshView.measuredHeight)
    }

    override fun onRefreshScroll(target: View,refreshHeader: BaseRefreshHeader,refreshHeight:Int, dx: Int, dy: Int,maxScroll:Int, consumed: IntArray) {
        var dy=dy
        //向上防止越界
        if(0>dy){
            //往上滑
            if(maxScroll<target.top){
                dy= 0
            } else if(maxScroll<target.top-dy){
                dy=target.top-maxScroll
            }
        }
        if(0>dy&&!ViewCompat.canScrollVertically(target,dy)){
            consumed[1]=dy
            target.offsetTopAndBottom(-dy)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
            debugLog("onNestedPreScroll_:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
        } else if(target.top>0){
            if(target.top-dy>maxScroll){
                dy=target.top-maxScroll
            }
            debugLog("onNestedPreScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
            consumed[1]=dy
            target.offsetTopAndBottom(-dy)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
            debugLog("onNestedPreScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
        }
    }


    override fun onRefreshPreFling(target: View, velocityX: Float, velocityY: Float,refreshHeight:Int): Boolean {
        //这里以模式进行约束,因为,Front模式列表位置一直固定
        if(target.top>0&&!layout.isRefreshing()){
            if(0>velocityY||refreshHeight<=target.top){
                debugLog("startScroll>: velocityY:$velocityY top:${target.top}")
                setRefreshing()
                offsetHeaderTopAndBottom(target,target.top-refreshHeight)
            } else {
                offsetHeaderTopAndBottom(target, target.top) { layout.setRefreshState(RefreshState.NONE) }
                debugLog("startScroll<: velocityY:$velocityY top:${target.top}")
            }
        }
        if(layout.isRefreshing()){
            //处在刷新状态,松手回到刷新处
            debugLog("startScroll: velocityY:$velocityY top:${target.top}")
            if(refreshHeight<=target.top){
                offsetHeaderTopAndBottom(target,target.top-refreshHeight)
            } else {
                offsetHeaderTopAndBottom(target,target.top)
            }
        }
        //让列表在非刷新模式下一直可以滚动
        return false
    }
    override fun onStopRefreshScroll(target: View,refreshHeight:Int) {
        //非刷新状态操作
        debugLog("onStopRefreshScroll:top:${target.top} ${layout.getRefreshState()}")
        if(!layout.isRefreshState(RefreshState.REFRESHING_COMPLETE)){
            if(refreshHeight<=target.top){
                if(!layout.isRefreshing()){
                    setRefreshing()
                }
                offsetHeaderTopAndBottom(target,target.top-refreshHeight)
            } else {
                offsetHeaderTopAndBottom(target,target.top){ layout.setRefreshState(RefreshState.NONE) }
            }
        } else {
            //处在刷新状态,松手回到刷新处
            offsetHeaderTopAndBottom(target,target.top)
        }
    }

    override fun autoRefresh(refreshHeight:Int, smooth: Boolean) {
        val refreshHeader = layout.getRefreshHeader()
        val refreshView = layout.getRefreshView<View>()
        if(smooth){
            offsetHeaderTopAndBottom(refreshView,refreshView.top-refreshHeight,{
                onRefreshScrollChanged(refreshHeader,layout.refreshHeight) }){ setRefreshing() }
        } else {
            refreshView.offsetTopAndBottom(refreshView.top-refreshHeight)
            onRefreshScrollChanged(refreshHeader,refreshHeight)
            layout.invalidate()
            setRefreshing()
        }
    }

    override fun onRefreshComplete(action: (() -> Unit)?) {
        val refreshView = layout.getRefreshView<View>()
        debugLog("onRefreshComplete:${refreshView.top}")
        offsetHeaderTopAndBottom(refreshView,refreshView.top){
            layout.setRefreshState(RefreshState.NONE)
            action?.invoke()
        }
    }

    /**
     * 滑动变化回调
     */
    private fun onRefreshScrollChanged(refreshHeader: BaseRefreshHeader,refreshHeight:Int){
        if(!layout.isRefreshing()){
            val refreshView = layout.getRefreshView<View>()
            val fraction=Math.min(1f,refreshView.top*1f/refreshHeight)
            refreshHeader.onScrollOffset(fraction)
            layout.setRefreshState(if(1f>=fraction) RefreshState.RELEASE_TO_REFRESHING else RefreshState.RELEASE_TO_CANCEL)
            debugLog("onScrollChanged: fraction:$fraction")
        }
    }

}
