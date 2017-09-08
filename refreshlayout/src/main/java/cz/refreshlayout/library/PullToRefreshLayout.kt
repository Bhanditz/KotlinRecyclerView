package cz.refreshlayout.library

import android.content.Context
import android.support.annotation.FloatRange
import android.support.annotation.IntDef
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.util.Log
import android.view.*
import cz.refreshlayout.library.header.BaseRefreshHeader
import cz.refreshlayout.library.header.MaterialDesignHeader
import cz.refreshlayout.library.header.WalletHeader
import cz.refreshlayout.library.strategy.*
import java.util.*

/**
 * Created by cz on 2017/7/27.
 * 本控件为一个下拉刷新控件的核心控件类,主要为满足以下设计
 * 1:强大的扩展性,需要支持目前近乎所有的下拉刷新需求
 * 2:尽可能做到最好的刷新体验.解决手势的拉动连贯性问题(通过NestedScrollingParent)
 *
 * 为实现以上功能,所以重新设计了下拉刷新所有模块,最为核心的为:{@link cz.refreshlayout.library.strategy.BaseStrategy}
 */
open class PullToRefreshLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr),NestedScrollingParent {
    companion object {
        const val STRATEGY_FOLLOW = 0x00
        const val STRATEGY_OVERLAP = 0x01
        const val STRATEGY_FRONT = 0x02
        const val STRATEGY_SCROLL = 0x03

        const val HEADER_WALLET=0x00
        const val HEADER_MATERIAL=0x01

        @IntDef(value = *longArrayOf(STRATEGY_FOLLOW.toLong(), STRATEGY_OVERLAP.toLong(), STRATEGY_FRONT.toLong(), STRATEGY_SCROLL.toLong()))
        annotation class Strategy
    }

    private val scroller:ScrollerCompat=ScrollerCompat.create(context)
    //以下俩个标记共存的原因为,系统nestedScroll的流程为 首次先触发onStartNestedScroll->onStopNestedScroll,
    // 再触发onStartNestedScroll->onNestedPreScroll->fling状态.所以必须有两个标记配置
    private var strategy:BaseStrategy= FollowStrategy(this)
    private var refreshHeader:BaseRefreshHeader= WalletHeader(context)
    private var refreshView:View?=getTargetRefreshView()
    private val refreshItem=RefreshItem()
    var refreshHeight=0
        get() = refreshHeader.headerView.measuredHeight
    private var maxRefreshScroll=0
        get() = refreshHeight+refreshItem.maxOffsetScroll
    private var refreshListener:OnPullToRefreshListener?=null

    constructor(context: Context):this(context,null,R.attr.pullToRefreshLayout){
        //直接new出控件,不走onFinishInflate,需要手动回调,初始化数据
        onFinishInflate()
    }

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,R.attr.pullToRefreshLayout)

    init {
        context.obtainStyledAttributes(attrs,R.styleable.PullToRefreshLayout,R.attr.pullToRefreshLayout,R.style.PullToRefreshLayout).apply{
            setResistance(getFloat(R.styleable.PullToRefreshLayout_pl_resistance,1f))
            setMaxOffsetScroll(getDimension(R.styleable.PullToRefreshLayout_pl_maxOffsetScroll,0f).toInt())
            setMinRefreshDuration(getInteger(R.styleable.PullToRefreshLayout_pl_minRefreshDuration,0))
            setHeaderTypeInner(getInt(R.styleable.PullToRefreshLayout_pl_headerType,HEADER_WALLET))
            setRefreshModeInner(RefreshMode.values()[getInt(R.styleable.PullToRefreshLayout_pl_refreshMode,RefreshMode.BOTH.ordinal)])
            //此处直接赋值
            strategy=getHeaderStrategy(getInt(R.styleable.PullToRefreshLayout_pl_headerStrategy,STRATEGY_FOLLOW))
            recycle()
        }
    }

    /**
     * 设置最短刷新时间
     */
    fun setMinRefreshDuration(duration: Int) {
        this.refreshItem.minRefreshDuration=duration
    }

    /**
     * 设置阻力值,默认为1f,如果为2f,则为两倍阻力
     * @param resistance 阻力
     */
    fun setResistance(@FloatRange(from = 1.0,to = 4.0) resistance: Float) {
        this.refreshItem.resistance=resistance
    }

    /**
     * 设置滑动的最大偏移值,默认为0,则由头来决定大小,给定值会附加到到可拖动高度
     */
    fun setMaxOffsetScroll(size: Int) {
        this.refreshItem.maxOffsetScroll=size
    }

    /**
     * 根据一个己有的strategy映射值,设置一个策略
     */
    fun setHeaderStrategy(@Strategy strategy: Int) {
        setHeaderStrategy(getHeaderStrategy(strategy))
    }

    /**
     * 根据一个分类,获得刷新策略
     */
    private fun getHeaderStrategy(position:Int):BaseStrategy= when(position){
        STRATEGY_FRONT->FrontStrategy(this)
        STRATEGY_OVERLAP->OverlapStrategy(this)
        STRATEGY_SCROLL->ScrollStrategy(this)
        else->FollowStrategy(this)
    }


    /**
     * 设置刷新头策略对象,该策略对象负责刷新头,与列表的排版,与各种滑动事件.
     * @param strategy 一个策略的实现体
     */
    open fun setHeaderStrategy(strategy: BaseStrategy) {
        this.strategy=strategy
        //移除所有控件
        removeAllViews()
        //重新添加所有控件
        strategy.onAddRefreshView(refreshHeader.headerView,refreshView)
        //重新排版所有控件
        strategy.onRefreshViewLayout(refreshHeader.headerView,refreshView,paddingLeft,paddingTop,width-paddingRight,height-paddingBottom)
    }

    private fun setHeaderTypeInner(type:Int){
        this.refreshHeader=when(type){
            HEADER_MATERIAL->MaterialDesignHeader(context)
            else ->WalletHeader(context)
        }
    }

    private fun setRefreshModeInner(refreshMode: RefreshMode){
        this.refreshItem.refreshMode=refreshMode
    }

    open fun setRefreshMode(refreshMode: RefreshMode){
        this.refreshItem.refreshMode=refreshMode
    }

    fun getRefreshMode()=refreshItem.refreshMode

    fun refreshDisable(): Boolean =refreshItem.refreshMode.disable()

    fun enableStart(): Boolean =refreshItem.refreshMode.enableStart()

    fun enableEnd(): Boolean =refreshItem.refreshMode.enableEnd()

    /**
     * 获得刷新策略
     */
    fun getHeaderStrategy()=strategy

    /**
     * 获得当前阻力
     */
    fun getResistance()=refreshItem.resistance

    /**
     * 获得刷新控件体
     */
    protected open fun getTargetRefreshView():View?=null

//    禁用外部添加控件
//    override fun addView(child: View)=throw IllegalStateException("PullToRefreshLayout can't add view!")
//    override fun addView(child: View, index: Int)=throw IllegalStateException("PullToRefreshLayout can't add view!")
//    override fun addView(child: View, params: ViewGroup.LayoutParams) =throw IllegalStateException("PullToRefreshLayout can't add view!")
//    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams)=throw IllegalStateException("PullToRefreshLayout can't add view!")
//

    override fun onFinishInflate() {
        super.onFinishInflate()
        val refreshView= refreshView?:if(1==childCount) getChildAt(0)
        else throw IllegalArgumentException("PullToRefreshLayout can only have one child view or use#getRefreshHeaderView!")
        addRefreshView(refreshView)
    }

    override fun computeScroll() {
        super.computeScroll()
        if(!scroller.isFinished&&scroller.computeScrollOffset()){
            scrollTo(scroller.currX,scroller.currY)
            postInvalidate()
        }
    }

    /**
     * 添加刷新控件,添加前会移除刷新头以及刷新列表,并根据策略重新排版
     */
    open fun addRefreshView(view:View){
        refreshView=view
        //移除所有控件
        removeAllViews()
        //策略控制控件添加,此处可决定,先加头,还是先加尾
        strategy.onAddRefreshView(refreshHeader.headerView,refreshView)
    }

    /**
     * 方法测量,此方法也由策略决定,本控件大小,因为每种策略控件排版都不同.有的为上下,有的为叠加.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val refreshView=refreshView?:return
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var measureWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        if (View.MeasureSpec.UNSPECIFIED == widthMode) {
            measureWidth = refreshView.measuredWidth
        }
        if (View.MeasureSpec.UNSPECIFIED == heightMode) {
            measureHeight = refreshView.measuredHeight
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        debugLog("onLayout:$changed l:$l t:$t r:$r b:$b width:$measuredWidth $measuredHeight $width $height $paddingLeft $paddingRight")
        val refreshView=refreshView?:return
        if(changed){
            //策略控制排版控件,changed触发整体重新排版,非变化时,不能触发,否则会影响现有的刷新头动作
            strategy.onRefreshViewLayout(refreshHeader.headerView,refreshView,l,t,r,b)
        } else {
            //回调layout时,手动触发排版列表,因为RecyclerView等控件若不触发会影响条目显示
            refreshView.layout(paddingLeft, paddingTop, paddingLeft+refreshView.measuredWidth, paddingTop+refreshView.measuredHeight)
        }
    }

    /**
     * 设置刷新头,设置时会移除刷新头以及刷新列表,并根据策略重新排版
     */
    open fun setRefreshHeader(header:BaseRefreshHeader){
        removeAllViews()//移除所有控件
        this.refreshHeader=header//重置刷新头
        //策略控制控件添加,此处可决定,先加头,还是先加尾
        strategy.onAddRefreshView(refreshHeader.headerView,refreshView)
    }

    /**
     * 设置当前滑动状态,在部分时候,可能会需要重置刷新状态,如数据列表,己加载完毕,但又重置另一个分类,此时就需要再次重头加载,并重置状态
     * @param state 设定的滑动状态
     */
    open fun setRefreshState(state:RefreshState){
        if(refreshItem.refreshState!=state){
            debugLog("setRefreshState:$state")
            refreshItem.refreshState=state
            refreshHeader.onRefreshStateChange(refreshItem.refreshState)
        }
    }

    /**
     * 获得当前滑动Scroller对象
     */
    fun getRefreshScroller()=scroller

    /**
     * 获得当前刷新状态
     */
    fun getRefreshState()=refreshItem.refreshState

    /**
     * 匹配当前滑动状态
     * @param state 需要匹配的状态
     */
    fun isRefreshState(state: RefreshState):Boolean=refreshItem.refreshState==state

    /**
     * 当前是否处于刷新状态
     */
    fun isRefreshing()=isRefreshState(RefreshState.REFRESHING)||
            isRefreshState(RefreshState.REFRESHING_DRAGGING)||
            isRefreshState(RefreshState.REFRESHING_COMPLETE)

    fun onRefreshComplete()=onRefreshComplete(null)
    /**
     * 滑动完毕后状态重置事件
     */
    open fun onRefreshComplete(action:(()->Unit)?=null){
        //本次刷新时间
        val refreshTime= System.currentTimeMillis() - refreshItem.startRefreshTime
        if(refreshItem.minRefreshDuration<refreshTime){
            //直接回调
            callRefreshComplete(action)
        } else {
            //延持回调
            postDelayed({ callRefreshComplete(action) },refreshItem.minRefreshDuration-refreshTime)
        }
    }

    /**
     * 回调刷新完成事件
     */
    private fun callRefreshComplete(action:(()->Unit)?=null){
        //当不为用户刷新拖动状态时,仅为刷新状态时,置为刷新完毕
        if((isRefreshState(RefreshState.REFRESHING)||
                isRefreshState(RefreshState.REFRESHING_COMPLETE))&&
                !isRefreshState(RefreshState.REFRESHING_DRAGGING)) {
            debugLog("callRefreshComplete:${refreshItem.refreshState}")
            this.setRefreshState(RefreshState.REFRESHING_COMPLETE)
            this.refreshHeader.onRefreshComplete {
                this.strategy.onRefreshComplete(action)
            }
        } else if(isRefreshState(RefreshState.REFRESHING_DRAGGING)){
            //当前为拖动状态,执行事件,并标记为刷新己完成
            debugLog("callRefreshComplete drag:${refreshItem.refreshState}")
            action?.invoke()
            this.setRefreshState(RefreshState.REFRESHING_COMPLETE)
            this.refreshHeader.onRefreshComplete()
        }
    }

    /**
     * 获得当前刷新控件
     */
    fun<V:View> getRefreshView():V= refreshView as V

    /**
     * 获得当前刷新头
     */
    fun getRefreshHeader()=refreshHeader

    /**
     * 获得当前刷新头控件
     */
    fun getRefreshHeaderView()=refreshHeader.headerView

    /**
     * 手动回调刷新事件
     */
    fun callRefreshListener(){
        //记录本次刷新时间
        refreshItem.startRefreshTime=System.currentTimeMillis()
        //直接回调
        refreshListener?.onRefresh()
    }

    /**
     * 启动刷新列内容体
     * @param startX 滑动起始x坐标
     * @param startY 滑动起始y坐标
     * @param dx 滑动x轴偏移量
     * @param dy 滑动y轴偏移量
     */
    fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int,duration:Int=250){
        debugLog("startScroll:$startY dy:$dy")
        scroller.startScroll(startX, startY, dx, dy,duration)
        invalidate()
    }

    /**
     * 自动刷新
     * @param smooth 是否绘制滚动
     */
    open fun autoRefresh(smooth:Boolean=true){
        if(!isRefreshing()){
            setRefreshState(RefreshState.START_PULL)
            strategy.autoRefresh(refreshHeight,smooth)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when(ev.actionMasked){
            MotionEvent.ACTION_DOWN->{
                if(!scroller.isFinished){
                    scroller.abortAnimation()
                    invalidate()
                }
                //设定状态
                if(isRefreshing()){
                    //设定为刷新但用户又重新拖动状态
                    setRefreshState(RefreshState.REFRESHING_DRAGGING)
                } else {
                    //设定为开始拖动
                    setRefreshState(RefreshState.START_PULL)
                }
            }
            MotionEvent.ACTION_UP->{
                //当两个标记都为false时,代表此时事件被内部滑动控件消费掉.所以手动将isNestedPreScroll置为true,以响应事件
                onStopScroll(refreshView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean{
        //这里决定根据模式是否拦截
        return refreshItem.refreshMode.enableStart()&&0!=(ViewCompat.SCROLL_AXIS_VERTICAL and nestedScrollAxes)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray){
        //控制顶头控件刷新动作,计算阻力
        debugLog("onNestedPreScroll:$dx $dy ${Arrays.toString(consumed)}")
        val destY=(dy/refreshItem.resistance).toInt()
        strategy.onRefreshScroll(target,refreshHeader,refreshHeight,dx,destY, maxRefreshScroll,consumed)
        //消耗值,consumed会在拖动过程中由具体策略自己计算出消费值,策略计算完毕后,在外围再加一次
        consumed[1]+=dy-destY
        debugLog("onNestedPreScroll after:$dx $dy $destY ${Arrays.toString(consumed)}")
    }


    /**
     * 终止滑动事件
     */
    fun onStopScroll(target:View?){
        val target=target?:return
        if(isRefreshState(RefreshState.REFRESHING_DRAGGING)){
            //置为刷新状态
            setRefreshState(RefreshState.REFRESHING)
        }
        //常规停止滑动状态
        strategy.onStopRefreshScroll(target,refreshHeight)
        invalidate()
    }

    /**
     * 此处不使用其作为事件终止回调,因为有些方法,消费了事件,导致此方法不回调
     */
    override fun onStopNestedScroll(target: View){
//        debugLog("onStopNestedScroll:$isNestedPreScroll $isNestedFling ${refreshItem.refreshState}")
    }


    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean{
        val preFling = strategy.onRefreshPreFling(target, velocityX, velocityY, refreshHeight)
        invalidate()
        return preFling
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int){}

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int){}

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean=false

    override fun getNestedScrollAxes(): Int=ViewCompat.SCROLL_AXIS_VERTICAL

    fun setOnRefreshListener(listener:OnPullToRefreshListener){
        refreshListener=listener
    }

    interface OnPullToRefreshListener {
        fun onRefresh()
    }

    /**
     * 刷新状态维护对象
     */
    inner class RefreshItem{
        //当前刷新头状态
        var refreshState=RefreshState.NONE
        //阻力值
        var resistance=1f
        //最大刷新附加空间
        var maxOffsetScroll=0
        //最短刷新时间
        var minRefreshDuration=0
        //刷新模式
        var refreshMode=RefreshMode.BOTH
        //当前刷新时间记录,用以判断最小刷新时间
        var startRefreshTime:Long=0
    }


}

/**
 * 此方法作用域仅在包内,包外无效
 */
val DEBUG = true
internal inline fun<reified T> T.debugLog(message:String){
    if(DEBUG){
        val item=this as Any
        Log.e(item::class.java.simpleName,message)
    }
}
