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
 * 一个展示列表背后刷新头策略,用于仿UC浏览器等,网页下拉后展示 uc内核此类
 */
class ScrollStrategy(layout: PullToRefreshLayout) : BaseStrategy(layout) {

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
            debugLog("onNestedPreScroll_:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
        } else if(target.top>0){
            if(target.top-dy>maxScroll){
                dy=target.top-maxScroll
            }
            debugLog("onNestedPreScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
            consumed[1]=dy
            target.offsetTopAndBottom(-dy)
            debugLog("onNestedPreScroll:${Arrays.toString(consumed)} dy:$dy top:${target.top} $maxScroll")
        }
    }


    override fun onRefreshPreFling(target: View, velocityX: Float, velocityY: Float,refreshHeight:Int): Boolean {
        offsetHeaderTopAndBottom(target,target.top)
        return false
    }
    override fun onStopRefreshScroll(target: View,refreshHeight:Int) {
        offsetHeaderTopAndBottom(target,target.top)
    }

    override fun onRefreshComplete(action: (() -> Unit)?) {
        val refreshView = layout.getRefreshView<View>()
        offsetHeaderTopAndBottom(refreshView,refreshView.top){
            layout.setRefreshState(RefreshState.NONE)
            action?.invoke()
        }
    }

    override fun autoRefresh(refreshHeight:Int, smooth: Boolean) {
        val refreshView = layout.getRefreshView<View>()
        if(smooth){
            offsetHeaderTopAndBottom(refreshView,refreshView.top-refreshHeight){
                setRefreshing()
                //100毫秒后自动收起
                layout.postDelayed({ onRefreshComplete() },100)
            }
        } else {
            refreshView.offsetTopAndBottom(refreshView.top-refreshHeight)
            layout.invalidate()
            setRefreshing()
        }
    }

}
