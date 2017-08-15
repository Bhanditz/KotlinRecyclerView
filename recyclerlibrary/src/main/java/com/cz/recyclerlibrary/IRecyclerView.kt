package com.cz.recyclerlibrary

import android.support.v7.widget.RecyclerView
import android.view.View
import com.cz.recyclerlibrary.callback.OnItemClickListener

/**
 * Created by czz on 2016/8/20.
 */
interface IRecyclerView {

    /**
     * set a RecyclerView.Adapter
     */
    var adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?

    var itemCount:Int

    /**
     * set a LayoutManager
     */
    var layoutManager: RecyclerView.LayoutManager?

    /**
     * get header view count
     * @return header view count
     */
    var headerViewCount: Int

    /**
     * get footer view count
     * @return view count
     */
    var footerViewCount: Int

    /**
     * set a RecyclerView.ItemAnimator
     */
    var itemAnimator: RecyclerView.ItemAnimator?

    /**
     * add a new header view,when view is null throws a NullPointerException
     * @param view
     */
    fun addHeaderView(view: View?)

    /**
     * remove a exist view,when view is null throws a NullPointerException
     * @param view
     */
    fun removeHeaderView(view: View?)

    /**
     * remove header view by index,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param index
     */
    fun removeHeaderView(index: Int)

    /**
     * add a new footer view,when view is null throws a NullPointerException
     * @param view
     */
    fun addFooterView(view: View)

    /**
     * remove a exist header view,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param view
     */
    fun removeFooterView(view: View)

    /**
     * remove header view by index,when index < 0 or < getHeaderViewCount() throw an IndexOutOfBoundsException
     * @param index
     */
    fun removeFooterView(index: Int)

    fun setHasStableIds(hasStableId:Boolean)

    fun addOnScrollListener(listener: RecyclerView.OnScrollListener)

    fun removeOnScrollListener(listener: RecyclerView.OnScrollListener)

    /**
     * set recycler view item click listener
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener)

    fun setOnPullFooterToRefreshListener(listener: PullToRefreshRecyclerView.OnPullFooterToRefreshListener)

    /**
     * set footer retray listener;
     * @param listener
     */
    fun setOnFootRetryListener(listener: View.OnClickListener)


}
