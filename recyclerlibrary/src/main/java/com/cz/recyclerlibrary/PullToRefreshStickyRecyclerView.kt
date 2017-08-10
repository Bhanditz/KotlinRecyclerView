package com.cz.recyclerlibrary

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cz.recyclerlibrary.callback.StickyCallback
import com.cz.recyclerlibrary.strategy.GroupingStrategy

/**
 * Created by Administrator on 2017/5/20.
 * 兼容PullToRefreshRecyclerView所有功能的StickyRecyclerView
 * 实现功能:
 * 1:增加任一布局的Sticky效果
 * 2:可动态配置数据的,以及更改StickyView大小
 * 3:支持布局/xml内直接写StickyView
 * 4:配合GroupingStrategy 最大化减少分组逻辑
 * [com.cz.recyclerlibrary.strategy.GroupingStrategy]

 * 使用:
 * 数据适配器继承BaseViewAdapter 且实现StickyCallback接口
 * [StickyCallback]

 * 示例:
 * 1:app/ui/sticky/Sticky1SampleActivity 演示:
 * 2:app/ui/sticky/Sticky2SampleActivity
 * 3:app/ui/sticky/Sticky3SampleActivity
 * 4:app/ui/sticky/Sticky4SampleActivity 演示GridLayoutManager的Sticky效果
 */
class PullToRefreshStickyRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : PullToRefreshRecyclerView(context, attrs, defStyleAttr) {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var observer: AdapterDataObserver? = null
    private var listener: StickyScrollListener? = null
    private var stickyView: View? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshStickyRecyclerView)
        setStickyView(a.getResourceId(R.styleable.PullToRefreshStickyRecyclerView_pv_stickyView, View.NO_ID))
        a.recycle()
    }

    override fun onFinishInflate() {
        removeLayoutStickyView()
        super.onFinishInflate()
    }

    /**
     * 获取布局配置sticky view对象
     */
    private fun removeLayoutStickyView() {
        val childCount = childCount
        if (0 < childCount && null == stickyView) {
            for (i in 0..childCount - 1) {
                val childView = getChildAt(i)
                val layoutParams = childView.layoutParams
                if (layoutParams is PullToRefreshStickyRecyclerView.LayoutParams) {
                    if (layoutParams.layoutStickyView) {
                        stickyView = childView
                        removeView(childView)
                        break
                    }
                }
            }
        }
    }

    fun setStickyView(resourceId: Int) {
        if (View.NO_ID != resourceId) {
            setStickyView(layoutInflater.inflate(resourceId, this, false))
        }
    }


    fun setStickyView(view: View) {
        if (null != this.stickyView) {
            removeView(this.stickyView)
        }
        //不添加,等待setAdapter时添加,避免出现无数据显示一个空的头情况
        this.stickyView = view
    }

    val itemCount: Int
        get() {
            val adapter = getAdapter()
            return adapter.itemCount
        }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        stickyView?.let { it.layout(l, 0, r, it.measuredHeight) }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        super.setAdapter(adapter)
        if (adapter !is StickyCallback<*>) {
            throw IllegalArgumentException("RecyclerView.Adapter must be implements StickyCallback!")
        } else if (null != stickyView) {
            removeView(stickyView)
            addView(stickyView)
            val targetView = refreshView
            targetView.removeOnScrollListener(listener)
            listener = StickyScrollListener(adapter)
            targetView.addOnScrollListener(listener)
            if (null == observer) {
                observer = AdapterDataObserver(adapter)
            } else {
                adapter.unregisterAdapterDataObserver(observer)
            }
            adapter.registerAdapterDataObserver(observer)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return PullToRefreshStickyRecyclerView.LayoutParams(context, attrs)
    }

    class LayoutParams(c: Context, attrs: AttributeSet) : PullToRefreshRecyclerView.LayoutParams(c, attrs) {
        var layoutStickyView: Boolean = false

        init {
            val a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshStickyRecyclerView)
            layoutStickyView = a.getBoolean(R.styleable.PullToRefreshStickyRecyclerView_pv_layoutStickyView, false)
            a.recycle()
        }
    }

    internal inner class AdapterDataObserver(private val callback: StickyCallback<*>) : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            //此处,当数据完全更新后,滑动到顶部,并更新显示头,否则会出现头与数据列不一致情况
            val firstVisiblePosition = firstVisiblePosition - headerViewCount
            val groupingStrategy = callback.getGroupingStrategy()
            val itemCount = itemCount
            val startIndex = groupingStrategy.getGroupStartIndex(firstVisiblePosition)
            if (startIndex < itemCount) {
                callback.initStickyView(stickyView!!, startIndex)
            }
        }
    }

    internal inner class StickyScrollListener(private val callback: StickyCallback<*>) : RecyclerView.OnScrollListener() {
        private val groupingStrategy: GroupingStrategy<*> = callback.getGroupingStrategy()
        private var lastVisibleItemPosition: Int = 0

        init {
            //初始化第一个节点信息,若数据罗多,延持到滑动时,会导致初始化第一个失败
            val itemCount = itemCount
            if (0 < itemCount) {
                this.callback.initStickyView(stickyView!!, 0)
            }
            this.lastVisibleItemPosition = RecyclerView.NO_POSITION
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val stickyView=stickyView?:return
            val layoutManager = layoutManager?:return
            var spanCount = 1
            if (layoutManager is GridLayoutManager) {
                spanCount = layoutManager.spanCount
            }
            val headerViewCount = headerViewCount
            val firstVisibleItemPosition = firstVisiblePosition
            if (firstVisibleItemPosition < headerViewCount) {
                stickyView.visibility = View.GONE
            } else {
                stickyView.visibility = View.VISIBLE
                val realVisibleItemPosition = firstVisibleItemPosition - headerViewCount
                //初始化当前位置Sticky信息
                val lastRealPosition = realVisibleItemPosition + spanCount
                for (position in realVisibleItemPosition..lastRealPosition) {
                    if (lastVisibleItemPosition != firstVisibleItemPosition && groupingStrategy.isGroupIndex(position)) {
                        lastVisibleItemPosition = firstVisibleItemPosition
                        val startIndex = groupingStrategy.getGroupStartIndex(realVisibleItemPosition)
                        if (startIndex < layoutManager.itemCount) {
                            callback.initStickyView(stickyView, startIndex)
                        }
                        break
                    }
                }
                stickyView.translationY = 0f
                //在这个范围内,找到本页内可能出现的下一个阶段的条目位置.
                val stickyPosition = findStickyPosition(realVisibleItemPosition + 1, lastVisiblePosition)
                if (RecyclerView.NO_POSITION != stickyPosition) {
                    val nextAdapterView = layoutManager.findViewByPosition(stickyPosition + headerViewCount)
                    if (null != nextAdapterView && nextAdapterView.top < stickyView.height) {
                        stickyView.translationY = (nextAdapterView.top - stickyView.height).toFloat()
                    }
                }
            }
        }

        fun findStickyPosition(position: Int, lastVisibleItemPosition: Int): Int {
            return (position..lastVisibleItemPosition).firstOrNull { groupingStrategy.isGroupIndex(it) } ?: RecyclerView.NO_POSITION
        }
    }
}
