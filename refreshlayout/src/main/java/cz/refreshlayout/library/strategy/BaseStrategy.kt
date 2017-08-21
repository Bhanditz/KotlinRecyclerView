package cz.refreshlayout.library.strategy

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import cz.refreshlayout.library.PullToRefreshLayout
import cz.refreshlayout.library.RefreshState
import cz.refreshlayout.library.debugLog
import cz.refreshlayout.library.header.BaseRefreshHeader

/**
 * Created by cz on 2017/7/28.
 * 抽象的策略扩展类
 */
abstract class BaseStrategy(val layout: PullToRefreshLayout) {
    private var valueAnimator:ValueAnimator?=null
    /**
     * 添加刷新控件,此处自由排版控件,比如将头先添加,或者头后添加,以回调此方法之前,控件均被移除,需要使用者自行添加
     */
    abstract fun onAddRefreshView(header:View, refreshView:View?)
    /**
     * 头刷新逻辑
     * @param target 当前列表控件
     * @param refreshHeader 当前刷新头对象
     * @param refreshHeight 当前刷新头高度
     * @param dx 滑动横向偏移值
     * @param dy 滑动纵向偏移值
     * @param maxScroll 最大滑动附加值,默认为0,如果为100,则可滑动区域会附加100
     * @param consumed 滑动消费值数组 默认为[0,0] 如果希望阻塞当前列表纵向滑动,只需要consumed[1]=dy
     */
    abstract fun onRefreshScroll(target: View,refreshHeader: BaseRefreshHeader,refreshHeight:Int, dx:Int, dy:Int,maxScroll:Int, consumed: IntArray)

    /**
     * 控件排版,此处可决定基础的列表与刷新头的排版,如上下排版,重叠或者其他任何排版
     * @param header 为当前刷新头控件
     * @param refreshView 当前列表控件
     */
    abstract fun onRefreshViewLayout(header:View,refreshView:View?,left:Int,top:Int,right:Int,bottom:Int)

    /**
     * 当刷新控件惯性滑动
     * @param target 为当前滚动目标
     * @param velocityX 当前横向加速值
     * @param velocityY 当前纵向加速值
     * @param refreshHeight 当前刷新头高度,以及计算刷新移动速率
     */
    abstract fun onRefreshPreFling(target: View, velocityX: Float, velocityY: Float,refreshHeight:Int): Boolean


    /**
     * 当手势开始拖动
     */
    fun onStartRefreshScroll(child: View, target: View, nestedScrollAxes: Int){
    }

    /**
     * 当刷新结束滚动
     * @param target 为当前滚动目标
     * @param refreshHeight 当前刷新头高度,以及计算刷新移动速率
     */
    abstract fun onStopRefreshScroll(target: View,refreshHeight:Int)

    /**
     * 自动刷新
     * @param refreshHeight 当前刷新头高度,以及计算刷新移动速率
     * @param smooth 设定是否缓慢移动过去
     */
    abstract fun autoRefresh(refreshHeight:Int, smooth:Boolean)

    /**
     * 刷新完成回调对象
     */
    abstract fun onRefreshComplete(action: (() -> Unit)?=null)

    /**
     * 设置状态为刷新,并回调刷新接口,此处确保回调与刷新只设定一次,因为外围可能多次调用
     */
    protected fun setRefreshing(){
        if(!layout.isRefreshing()){
            layout.setRefreshState(RefreshState.REFRESHING)
            layout.callRefreshListener()
        }
    }

    /**
     * 一个控制单个控件缓慢移动方法,为辅助一些刷新头以及列表滚动动作
     * @param v 移动操作控件
     * @param top 移动目标目标 如当前控件为100  top=200 则会执行100-200移动区间动画
     * @param update 速率更新回调对象
     * @param action 为动画结束事件
     */
    protected fun offsetHeaderTopAndBottom(v:View,top: Int,update:((Int)->Unit)?=null, action:(()->Unit)?=null) {
        debugLog("offsetHeaderTopAndBottom:$top")
        //此处有一个设计上的问题为,如果动画时间太长.而滑动时,想要与列表联动,则必须等此动画完成.
        // 因为此动画,可能在某个策略里,是还原列表/刷新头的动画,所以必须等他回原后,才可以进行自由的滑动逻辑
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.removeAllListeners()
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(top).apply {
            duration=200
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                private var lastValue: Int = 0
                override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                    val value = valueAnimator.animatedValue as Int
                    v.offsetTopAndBottom(lastValue - value)
                    update?.invoke(lastValue - value)
                    //记录每一桢上一次取值,用与下一次进行偏移量计算
                    lastValue = value
                }
            })
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    action?.invoke()
                }
            })
            start()
        }
    }
}