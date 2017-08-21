package cz.refreshlayout.library.header

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import cz.refreshlayout.library.RefreshState
import cz.refreshlayout.library.debugLog
import cz.refreshlayout.library.widget.WalletView

/**
 * Created by cz on 2017/8/2.
 */
class WalletHeader(context: Context) :BaseRefreshHeader(){

    private val walletView=WalletView(context).apply { setPadding(0, dp2px(context,16f).toInt(),0,dp2px(context,16f).toInt()) }
    private val minFraction=0.4f
    override val headerView: View
        get() = walletView

    private fun dp2px(context:Context,value:Float)=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.resources.displayMetrics)

    override fun onScrollOffset(fraction: Float) {
        //从minFraction开始,才计入运算速率
        if(minFraction<=fraction){
            walletView.setAnimationFraction((fraction-minFraction)/(1f-minFraction))
        }
        if(1f<=fraction){
            walletView.start()
        }
    }

    override fun onRefreshStateChange(state: RefreshState) {
        when (state) {
            RefreshState.START_PULL -> {
                walletView.visibility = View.VISIBLE
                walletView.stop()
            }
            RefreshState.REFRESHING ->
                walletView.startRotateAnimation()
            RefreshState.NONE -> {
                walletView.stop()
                walletView.stopRotateAnimation()
                walletView.visibility = View.GONE
            }
        }
    }

}