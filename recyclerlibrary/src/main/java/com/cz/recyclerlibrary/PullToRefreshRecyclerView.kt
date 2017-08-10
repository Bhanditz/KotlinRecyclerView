package com.cz.recyclerlibrary

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.support.annotation.IntDef
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.adapter.SelectAdapter
import com.cz.recyclerlibrary.callback.OnItemClickListener
import com.cz.recyclerlibrary.divide.SimpleItemDecoration
import com.cz.recyclerlibrary.footer.RefreshFrameFooter
import com.cz.recyclerlibrary.observe.DynamicAdapterDataObserve

import java.util.ArrayList

import cz.refreshlayout.library.BasePullToRefreshLayout
import cz.refreshlayout.library.RefreshMode


/**
 * Created by czz on 2016/8/13.
 * 这个控件,主要由几部分组成
 * 1:PullToRefreshLayout:另一个下拉刷新的加载库
 * 2:隔离封装的Adapter支持:
 * 其中控件内,提供的Adapter为:SelectAdapter,层级判断为:DynamicAdapter->RefreshAdapter->SelectAdapter
 * 其中DynamicAdapter为负责任一元素位置插入条目的扩展数据适配器.
 * RefreshAdapter为固定底部的Footer的数据适配器.
 * 而最上层的SelectAdapter,则提供类似ListView的selectMode选择功能的数据适配器.适配器需实现Selectable接口
 * 实现的功能为:
 * 1:recyclerView的下拉刷新,上拉加载,
 * 2:顶部以及,底部的控件自由添加,删除,中间任一位置控件添加,此为确保RecyclerView数据一致性.比如新闻类应用.可能为了广告,为了某些提示条目,还需要去适合到逻辑Adapter内.导致条目很难看.
 * 3:Adapter的条目选择功能.
 * 4:类ListView的Divide封装

 * 待优化/注意事件:
 * 1:当使用addDynamicView功能时,设置OnItemClickListener事件返回的position为插入的顺移的位置,
 * 比如1,9插入两个元素,当点击子元素为10位置元素时,将返回12,这时候如果想获取子条目位置,可以使用#getItemPosition方法.
 * 具体原因为,1 9 位置各插入一个条目,此时,点击第10个位置条目,真实子Adapter条目位置为8,取得8,但很难根据8还原为10.还原方式为while(0->8) !isDynamicItem()++ 效率很低.
 * 故此.只传回子类,也使用原始Position,使用时,调用DragRecyclerView的getItemPosition方法获取具体子条目位置

 * 2:addDynamicView此方法,有一个问题,暂时未找到原因:如果谁清楚,请帮助解决一下,所以不用notifyItemInserted改用notifyDataSetChanged,性能差一点,但不会报错.
 * // java.lang.IllegalArgumentException: Called removeDetachedView withBinary a view which is not flagged as tmp detached.ViewHolder{3c6be8ee position=17 id=-1, oldPos=-1, pLpos:-1}


 * 以上.2016/9/24
 */
open class PullToRefreshRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : BasePullToRefreshLayout(context, attrs, defStyleAttr), IRecyclerView {

    companion object {
        const val END_NONE = 0x00
        const val END_NORMAL = 0x01
        const val END_REFRESHING = 0x02

        const val CLICK = 0x00
        const val SINGLE_SELECT = 0x01
        const val MULTI_SELECT = 0x02
        const val RECTANGLE_SELECT = 0x03
    }

    @IntDef(value = *longArrayOf(CLICK.toLong(), SINGLE_SELECT.toLong(), MULTI_SELECT.toLong(), RECTANGLE_SELECT.toLong()))
    annotation class SelectMode

    private val adapter=SelectAdapter(null)
    private val itemDecoration: SimpleItemDecoration
    private var listener: OnPullFooterToRefreshListener? = null
    private var dataObserve = DynamicAdapterDataObserve(adapter)
    private var refreshState: Int = END_NONE
    private var dissatisfiedScrollLoad=false
    private val refreshFooter by lazy { RefreshFrameFooter(context) }
    protected val refreshView by lazy { getRefreshView<RecyclerView>() }

    constructor(context: Context) : this(context, null, 0) {
        addRefreshView(refreshView)
    }

    init {
        refreshState = END_NORMAL
        itemDecoration = SimpleItemDecoration()
        initFooterViewByMode(getRefreshMode())

        val a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView)
        setListDivide(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pv_listDivide))
        setListDivideHeight(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_listDivideHeight, 0f))
        setDivideHorizontalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideHorizontalPadding, 0f))
        setDivideVerticalPadding(a.getDimension(R.styleable.PullToRefreshRecyclerView_pv_divideVerticalPadding, 0f))
        setDissatisfiedScreenLoad(a.getBoolean(R.styleable.PullToRefreshRecyclerView_pv_dissatisfiedScreenLoad,true))
        setSelectModeInner(a.getInt(R.styleable.PullToRefreshRecyclerView_pv_choiceMode, CLICK))
        setSelectMaxCount(a.getInteger(R.styleable.PullToRefreshRecyclerView_pv_choiceMaxCount, SelectAdapter.MAX_COUNT))
        a.recycle()
    }

    override fun onFinishInflate() {
        initLayoutHeaderFooterItems()
        super.onFinishInflate()
    }

    /**
     * 初始化布局内配置头与毛控件
     */
    private fun initLayoutHeaderFooterItems() {
        val i = 0
        while (i < childCount) {
            val childView = getChildAt(i)
            val layoutParams = childView.layoutParams as LayoutParams
            if (LayoutParams.ITEM_HEADER == layoutParams.itemType) {
                adapter.addHeaderView(childView)
            } else if (LayoutParams.ITEM_FOOTER == layoutParams.itemType) {
                adapter.addFooterView(childView)
            }
            removeViewAt(0)
        }
    }

    override fun addRefreshView(view: View) {
        super.addRefreshView(view)
        this.refreshView.addItemDecoration(itemDecoration)
    }

    fun setListDivide(drawable: Drawable?) {
        itemDecoration.setDrawable(drawable)
    }

    fun setListDivideHeight(listDivideHeight: Float) {
        itemDecoration.setStrokeWidth(Math.round(listDivideHeight))
    }

    fun setDivideHorizontalPadding(padding: Float) {
        this.itemDecoration.setDivideHorizontalPadding(Math.round(padding))
    }

    fun setDivideVerticalPadding(padding: Float) {
        this.itemDecoration.setDivideVerticalPadding(Math.round(padding))
    }

    fun showHeaderViewDivide(show: Boolean) {
        this.itemDecoration.showHeaderDecoration(show)
        this.refreshView.invalidateItemDecorations()
    }

    fun showFooterViewDivide(show: Boolean) {
        this.itemDecoration.showFooterDecoration(show)
        this.refreshView.invalidateItemDecorations()
    }

    fun setSelectMode(@SelectMode mode: Int) {
        setSelectModeInner(mode)
    }

    private fun setSelectModeInner(mode: Int) {
        adapter.setSelectMode(mode)
        invalidate()
    }

    override fun setHasStableIds(hasStableId: Boolean) {
        adapter.setHasStableIds(hasStableId)
//        itemAnimator?.setSupportsChangeAnimations(false)
    }

    /**
     * 设置可选择条目最大数,仅针对MULTI_SELECT 有效
     * @param count
     */
    fun setSelectMaxCount(count: Int) {
        this.adapter.setSelectMaxCount(count)
    }

    /**
     * 设置初始不满一屏是否底部是否加载数据
     */
    fun setDissatisfiedScreenLoad(load:Boolean){
        this.dissatisfiedScrollLoad=load
    }

    override fun getTargetRefreshView(): View? {
        val recyclerView = RecyclerView(context)
        recyclerView.itemAnimator = null
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollStateChanged(newState)
            }
        })
        return recyclerView
    }
    override var itemAnimator: RecyclerView.ItemAnimator?
        set(value) {
            //TODO 未知原因,设置了stableId,后再设置动画,操作会崩溃
            setHasStableIds(false)
            refreshView.itemAnimator = value
        }
        get() = this.refreshView.itemAnimator

    override var layoutManager: RecyclerView.LayoutManager?
        set(value) {
            this.refreshView.layoutManager = value
            if (value is GridLayoutManager || value is StaggeredGridLayoutManager) {
                itemDecoration.setDivideMode(SimpleItemDecoration.GRID)
            } else if (value is LinearLayoutManager) {
                val orientation = value.orientation
                itemDecoration.setDivideMode(if (OrientationHelper.HORIZONTAL == orientation) SimpleItemDecoration.HORIZONTAL else SimpleItemDecoration.VERTICAL)
            }
        }
        get() = this.refreshView.layoutManager

    override var headerViewCount: Int=0
        get() = this.adapter.headerViewCount

    override var footerViewCount: Int=0
        get() = adapter.footerViewCount

    override fun addHeaderView(view: View) {
        checkNullObjectRef(view)
        adapter.addHeaderView(view)
        itemDecoration.setHeaderCount(headerViewCount)
    }

    override fun removeHeaderView(view: View) {
        checkNullObjectRef(view)
        adapter.removeDynamicView(view)
        itemDecoration.setHeaderCount(headerViewCount)
    }

    override fun removeHeaderView(index: Int) {
        checkIndexInBounds(index, headerViewCount)
        adapter.removeHeaderView(index)
        itemDecoration.setHeaderCount(headerViewCount)
    }

    override fun addFooterView(view: View) {
        checkNullObjectRef(view)
        adapter.addFooterView(view)
        itemDecoration.setFooterCount(footerViewCount)
    }

    override fun removeFooterView(view: View) {
        checkNullObjectRef(view)
        adapter.removeFooterView(view)
        itemDecoration.setFooterCount(footerViewCount)
    }

    override fun removeFooterView(index: Int) {
        checkIndexInBounds(index, footerViewCount)
        adapter.removeFooterView(index)
        itemDecoration.setFooterCount(footerViewCount)
    }


    fun addDynamicView(view: View?, position: Int) {
        if (null != view) {
            adapter.addDynamicView(view, position)
        }
    }

    fun removeDynamicView(view: View?) {
        if (null != view) {
            adapter.removeDynamicView(view)
        }
    }

    fun itemRangeGlobalRemoved(positionStart: Int, itemCount: Int) {
        adapter.itemRangeGlobalRemoved(positionStart, itemCount)
    }

    override fun addOnScrollListener(listener: RecyclerView.OnScrollListener) {
        this.refreshView.addOnScrollListener(listener)
    }

    override fun removeOnScrollListener(listener: RecyclerView.OnScrollListener) {
        this.refreshView.removeOnScrollListener(listener)
    }

    override fun setOnItemClickListener(listener: OnItemClickListener) {
        this.adapter.setOnItemClickListener(listener)
    }

    override fun setOnFootRetryListener(listener: View.OnClickListener) {
        this.refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_ERROR)
        this.refreshFooter.setOnFootRetryListener(listener)
    }


    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter.adapter?.unregisterAdapterDataObserver(dataObserve)
        adapter.registerAdapterDataObserver(dataObserve)
        this.adapter.adapter=adapter
        if(null==refreshView.adapter){
            refreshView.adapter=this.adapter
        } else {
            this.adapter.notifyDataSetChanged()
        }
    }

    val originalAdapter: RecyclerView.Adapter<*>
        get() = this.adapter

    fun getAdapter(): RecyclerView.Adapter<*> =this.adapter.adapter

    fun setRefreshFooterState(@RefreshFrameFooter.RefreshState state: Int) {
        refreshFooter.setRefreshState(state)
    }

    override fun setRefreshMode(refreshMode: RefreshMode) {
        super.setRefreshMode(refreshMode)
        initFooterViewByMode(refreshMode)
    }

    /**
     * 滚动到指定位置
     * @param position
     */
    fun scrollToPosition(position: Int)=refreshView.scrollToPosition(position)


    private fun initFooterViewByMode(mode: RefreshMode) {
        val footerView = refreshFooter.footerView
        if (mode.enableEnd()) {
            refreshState = END_NORMAL
            adapter.addRefreshFooterView(footerView)
            refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_LOAD)
            scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE)
        } else {
            refreshState = END_NONE
            adapter.removeRefreshFooterView(footerView)
        }
        itemDecoration.setFooterCount(footerViewCount)
    }

    /**
     * 查找 header/footer view
     * @param id
     * *
     * @return
     */
    fun findAdapterView(@IdRes id: Int): View? {
        var findView: View? = findViewById(id)
        if (null == findView) {
            findView = adapter.findRefreshView(id)
        }
        if (null == findView) {
            findView = adapter.findDynamicView(id)
        }
        return findView
    }

    /**
     * check object is a null,when object is null reference throw NullPointerException
     * @param obj
     */
    private fun checkNullObjectRef(obj: Any?) {
        if (null == obj) {
            throw NullPointerException("The header view is null!")
        }
    }

    /**
     * check index is out of bounds,when object is out of bounds  throw IndexOutOfBoundsException
     * @param index
     * *
     * @param count
     */
    private fun checkIndexInBounds(index: Int, count: Int) {
        if (0 > index || index >= count) {
            throw IndexOutOfBoundsException("index out of bounds!")
        }
    }

    /**
     * 获得子条目的位置

     * @param position
     * *
     * @return
     */
    fun getItemPosition(position: Int): Int {
        return position - adapter.getStartIndex(position)
    }

    /**
     * on recyclerView scroll state changed
     * @param state
     */
    private fun scrollStateChanged(state: Int) {
        val refreshMode = getRefreshMode()
        if (state == RecyclerView.SCROLL_STATE_IDLE && null != listener && refreshMode.enableEnd()) {
            val layoutManager = refreshView.layoutManager
            val firstVisiblePosition = firstVisiblePosition
            val lastVisibleItemPosition = lastVisiblePosition
            val itemCount = layoutManager.itemCount
            if (!dissatisfiedScrollLoad&&lastVisibleItemPosition - firstVisiblePosition >= itemCount - 1) {
                //当标记为不满一屏不加载,当不满一屏,直接置为加载完毕
                setFooterRefreshDone()
            } else if (lastVisibleItemPosition >= itemCount - 1 &&
                    layoutManager.itemCount >= layoutManager.childCount &&
                    refreshState == END_NORMAL && !refreshFooter.isRefreshDone) {
                //大于一屏,回调加载更多
                refreshState = END_REFRESHING
                listener?.onRefresh()
            }
        }
    }

    /**
     * get last visible position
     * @return last visible position
     */
    val lastVisiblePosition: Int
        get() {
            var lastVisibleItemPosition: Int
            val layoutManager = refreshView.layoutManager
            if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val spanCount = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(spanCount)
                lastVisibleItemPosition = spanCount.max() ?: spanCount[0]
            } else {
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            return lastVisibleItemPosition
        }

    /**
     * get first visible position
     * @return last visible position
     */
    val firstVisiblePosition: Int
        get() {
            var lastVisibleItemPosition: Int
            val layoutManager = refreshView.layoutManager
            if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val spanCount = IntArray(layoutManager.spanCount)
                layoutManager.findFirstVisibleItemPositions(spanCount)
                lastVisibleItemPosition = spanCount.max() ?: spanCount[0]
            } else {
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
            return lastVisibleItemPosition
        }

    override fun autoRefresh(smooth: Boolean) {
        scrollToPosition(0)//执行自动滚动时,自动将条目滑到0,这里自动刷新之所以post事件,是因为同时执行.scroll事件会被屏蔽.导致失效
        post { super@PullToRefreshRecyclerView.autoRefresh(smooth) }
    }

    fun onRefreshFootComplete() {
        if (END_REFRESHING == refreshState) {
            refreshState = END_NORMAL
            this.refreshView.requestLayout()
        }
    }

    fun setFooterRefreshDone() {
        refreshFooter.setRefreshState(RefreshFrameFooter.FRAME_DONE)
    }

    override fun setOnPullFooterToRefreshListener(listener: OnPullFooterToRefreshListener) {
        this.listener = listener
    }

    fun setRectangleSelectPosition(start: Int, end: Int) {
        this.adapter.setRectangleSelectPosition(start, end)
    }

    /*
     * 设置单选选择监听
     *
     * @param singleSelectListener
     */
    fun setOnSingleSelectListener(singleSelectListener: OnSingleSelectListener) {
        adapter.setOnSingleSelectListener(singleSelectListener)
    }

    var singleSelectPosition: Int
        get() = this.adapter.singleSelectPosition
        set(position) {
            this.adapter.singleSelectPosition = position
        }


    var multiSelectItems: List<Int>
        get() = this.adapter.multiSelectItems
        set(items) {
            this.adapter.multiSelectItems = items
        }


    val rectangleSelectPosition: IntRange
        get() = this.adapter.rectangleSelectPosition

    /*
    * 设置多选选择监听
    *
    * @param singleSelectListener
    */
    fun setOnMultiSelectListener(multiSelectListener: OnMultiSelectListener) {
        adapter.setOnMultiSelectListener(multiSelectListener)
    }

    /*
    * 设置截取选择监听
    *
    * @param singleSelectListener
    */
    fun setOnRectangleSelectListener(rectangleSelectListener: OnRectangleSelectListener) {
        adapter.setOnRectangleSelectListener(rectangleSelectListener)
    }

    /**
     * set bottom refresh listener
     */
    interface OnPullFooterToRefreshListener {
        fun onRefresh()
    }

    /**
     * 选择监听器
     */
    interface OnSingleSelectListener {
        fun onSingleSelect(v: View, newPosition: Int, oldPosition: Int)
    }

    interface OnMultiSelectListener {
        fun onMultiSelect(v: View, selectPositions: ArrayList<Int>, lastSelectCount: Int, maxCount: Int)
    }

    interface OnRectangleSelectListener {
        fun onRectangleSelect(startPosition: Int, endPosition: Int)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return PullToRefreshRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return PullToRefreshRecyclerView.LayoutParams(context, attrs)
    }

    open class LayoutParams : ViewGroup.LayoutParams {
        var itemType: Int = 0

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView)
            itemType = a.getInt(R.styleable.PullToRefreshRecyclerView_pv_adapterView, ITEM_HEADER)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: ViewGroup.LayoutParams) : super(source)

        companion object {
            val ITEM_HEADER = 0x00
            val ITEM_FOOTER = 0x01
            val ITEM_NONE = 0x02
        }
    }


}
