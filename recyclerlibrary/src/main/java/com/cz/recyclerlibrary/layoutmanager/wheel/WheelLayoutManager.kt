package com.cz.recyclerlibrary.layoutmanager.wheel

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cz.recyclerlibrary.debugLog

import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager


/**
 * Created by cz on 2017/9/22.
 */
class WheelLayoutManager(orientation: Int) : CenterLinearLayoutManager(orientation) {
    private var wheelCount: Int = 5

    fun setWheelCount(itemCount: Int) {
        this.wheelCount = itemCount
        removeAllViews()
        requestLayout()
    }

    /**
     * 当装载入 RecyclerView时,设置固定布局(即布局大小,由 LayoutManager 的 onMeasure 控件),以及添加滚动完居中监听
     * @param recyclerView
     */
    override fun onAttachedToWindow(recyclerView: RecyclerView) {
        //设置不自动RecyclerView 不自由排版,且固定尺寸
        isAutoMeasureEnabled = false
//        recyclerView.setHasFixedSize(true)
        super.onAttachedToWindow(recyclerView)
    }

    /**
     * 重新测量
     */
    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        if(state.isPreLayout) return
        val measuredWidth = View.MeasureSpec.getSize(widthSpec)
        var measuredHeight = View.MeasureSpec.getSize(heightSpec)
        if (0 == itemCount) {
            //没有元素时,让当前高度为0
            measuredHeight=0
        } else {
            /*
             * 拥有元素时,取第0个条目的高度设定为基准高度
             * 这里有二个bug,某些品牌手机,无法使用recycler.getViewForPosition(0)
             * 1:在onMeasure内,当设置isAutoMeasureEnabled = false与recyclerView.setHasFixedSize(true)后
             * onMeasure内,state一直处于未计算状态也就是state.itemPosition一直为0,所以获取第0个基准元素.会导致崩溃,所以我们直接采用adapter获取
             * 2:当不设置recyclerView.setHasFixedSize(true) 再通过recycler.getViewForPosition(0),可以获取到控件,但在
             * measureChildWithMargins(viewHolder.itemView, 0, 0)时,测试获得不同手机的measuredHeight高度不同!,所以这种方案也不可取
            */
            val adapter=recyclerView.adapter
            val viewType= adapter.getItemViewType(0)
            val viewHolder=adapter.createViewHolder(recyclerView,viewType)
            if (null != viewHolder) {
                measureChildWithMargins(viewHolder.itemView, 0, 0)
                measuredHeight = viewHolder.itemView.measuredHeight * wheelCount
            }
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

}
