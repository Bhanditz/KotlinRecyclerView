package cz.refreshlayout.library.header

import android.content.Context
import android.view.View
import cz.refreshlayout.library.RefreshState

/**
 * Created by cz on 2017/7/28.
 */
abstract class BaseRefreshHeader {
    abstract val headerView:View
    /**
     * 刷新速率变化
     */
    abstract fun onScrollOffset(fraction: Float)

    /**
     * 刷新状态更变
     */
    abstract fun onRefreshStateChange(state: RefreshState)

    /**
     * 刷新完成
     */
    open fun onRefreshComplete(action: (() -> Unit)?=null) =action?.invoke()

}