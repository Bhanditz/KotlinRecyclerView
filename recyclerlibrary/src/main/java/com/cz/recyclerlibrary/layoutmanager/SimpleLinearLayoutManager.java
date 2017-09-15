package com.cz.recyclerlibrary.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


/**
 * Created by cz on 2017/1/15.
 */

public class SimpleLinearLayoutManager extends RecyclerView.LayoutManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "SimpleLinearLayoutManager2";
    private final LayoutState layoutState;

    public SimpleLinearLayoutManager() {
        layoutState=new LayoutState();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int itemCount = getItemCount();
        if(0==itemCount||state.isPreLayout()){
            detachAndScrapAttachedViews(recycler);
        } else if(state.didStructureChange()){
            detachAndScrapAttachedViews(recycler);
//            updateLayoutStateToFillEnd(layoutState.mCurrentPosition,layoutState.mOffset);
            updateLayoutStateToFillEnd(0,0);
            fill(recycler,layoutState,state,0);
        }
    }

    private void recycleByLayoutState(RecyclerView.Recycler recycler, LayoutState layoutState, int absDistance) {
        if (DEBUG) {
            Log.e(TAG, "LayoutDirection:"+layoutState.mLayoutDirection+" mScrollingOffset:"+layoutState.mScrollingOffset);
        }
        if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
            recycleViewsFromEnd(recycler, layoutState.mScrollingOffset);
        } else {
            recycleViewsFromStart(recycler, layoutState.mScrollingOffset);
        }
    }

    /**
     * Recycles views that went out of bounds after scrolling towards the end of the layout.
     *
     * @param recycler Recycler instance of {@link RecyclerView}
     * @param dt       This can be used to add additional padding to the visible area. This is used
     *                 to detect children that will go out of bounds after scrolling, without
     *                 actually moving them.
     */
    private void recycleViewsFromStart(RecyclerView.Recycler recycler, int dt) {
        if (dt < 0) {
            if (DEBUG) {
                Log.e(TAG, "Called recycle from start with a negative value. This might happen"
                        + " during layout changes but may be sign of a bug");
            }
            return;
        }
        // ignore padding, ViewGroup may not clip children.
        final int limit = dt;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (getDecoratedBottom(child) + params.bottomMargin > limit) {// stop here
                recycleChildren(recycler, 0, i);
                Log.e(TAG,"decoratedEnd:"+getDecoratedBottom(child)+" scrollOffset:"+limit+" start:"+0+" end:"+i);
                return;
            }
        }
    }

    /**
     * Recycles views that went out of bounds after scrolling towards the start of the layout.
     *
     * @param recycler Recycler instance of {@link RecyclerView}
     * @param dt       This can be used to add additional padding to the visible area. This is used
     *                 to detect children that will go out of bounds after scrolling, without
     *                 actually moving them.
     */
    private void recycleViewsFromEnd(RecyclerView.Recycler recycler, int dt) {
        final int childCount = getChildCount();
        if (dt < 0) {
            if (DEBUG) {
                Log.e(TAG, "Called recycle from end with a negative value. This might happen"
                        + " during layout changes but may be sign of a bug");
            }
            return;
        }
        final int limit = getHeight() - dt;
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (getDecoratedTop(child) - params.topMargin < limit) {// stop here
                recycleChildren(recycler, childCount - 1, i);
                return;
            }
        }
    }

    private void recycleChildren(RecyclerView.Recycler recycler, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return;
        }
        if (DEBUG) {
            Log.e(TAG, "Recycling " + Math.abs(startIndex - endIndex) + " items");
        }
        if (endIndex > startIndex) {
            for (int i = endIndex - 1; i >= startIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
                Log.e(TAG, "removeAndRecycleViewAt " + i);
            }
        } else {
            for (int i = startIndex; i > endIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
                Log.e(TAG, "removeAndRecycleViewAt " + i);
            }
        }
    }

    /**
     * The magic functions :). Fills the given layout, defined by the layoutState. This is fairly
     * and with little change, can be made publicly available as a helper class.
     *
     * @param recycler        Current recycler that is attached to RecyclerView
     * @param layoutState     Configuration on how we should fill out the available space.
     * @param state           Context passed by the RecyclerView to control scroll steps.
     * @return Number of pixels that it added. Useful for scoll functions.
     */
    int fill(RecyclerView.Recycler recycler, LayoutState layoutState, RecyclerView.State state, int absDistance) {
        // max offset we should set is mFastScroll + available
        final int start = layoutState.mAvailable;
            // TODO ugly bug fix. should not happen
//        if (layoutState.mAvailable < 0) {
//            layoutState.mScrollingOffset += layoutState.mAvailable;
//        }
        recycleByLayoutState(recycler, layoutState,absDistance);
        int remainingSpace = layoutState.mAvailable;
        Log.e(TAG,"mScrollingOffset:"+layoutState.mScrollingOffset+" remainingSpace:"+remainingSpace +" start:"+start+" "+(start - layoutState.mAvailable));
        while (remainingSpace > 0 && layoutState.hasMore(state)) {
//        while (remainingSpace > 0) {
            int consume=layoutChunk(recycler, layoutState,state);
            layoutState.mOffset += consume * layoutState.mLayoutDirection;
//            if (!state.isPreLayout()) {
                layoutState.mAvailable -= consume;
                // we keep a separate remaining space because mAvailable is important for recycling
                remainingSpace -= consume;
//            }
        }
        return start - layoutState.mAvailable;
    }

    int layoutChunk(RecyclerView.Recycler recycler, LayoutState layoutState,RecyclerView.State state) {
        View view = layoutState.next(recycler,state);
        if (view == null) {
            // if we are laying out views in scrap, this may return null which means there is
            // no more items to layout.
            if(DEBUG) Log.e(TAG,"layoutChunk finished break!");
            return 0;
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (layoutState.mLayoutDirection == LayoutState.LAYOUT_END) {
            addView(view);
        } else {
            addView(view, 0);
        }
        measureChildWithMargins(view, 0, 0);

        int consumed = getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
        int left, top, right, bottom;
        left = getPaddingLeft();
        Log.e(TAG,"offset:"+layoutState.mOffset);
        right = left + getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
        if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
            bottom = layoutState.mOffset;
            top = layoutState.mOffset - consumed;
        } else {
            top = layoutState.mOffset;
            bottom = layoutState.mOffset + consumed;
        }
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        layoutDecorated(view, left + params.leftMargin, top + params.topMargin,
                right - params.rightMargin, bottom - params.bottomMargin);
        if (DEBUG) {
            Log.e(TAG, "laid out child at position " + getPosition(view) + ", with l:"
                    + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                    + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin));
        }
        return consumed;
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dy, recycler, state);
    }

    int scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        final int layoutDirection = dy > 0 ? LayoutState.LAYOUT_END : LayoutState.LAYOUT_START;
        final int absDy = Math.abs(dy);
        updateLayoutState(layoutDirection, absDy);
        final int consumed = layoutState.mScrollingOffset + fill(recycler, layoutState, state,absDy);
        //最顶部/最后一个,防止滑动超出
        final int scrolled = absDy > consumed ? layoutDirection * consumed : dy;
        offsetChildrenVertical(-scrolled);
        Log.e(TAG,"LAYOUT_END consumed:"+consumed+" dy:"+dy+" scrolled:"+scrolled+" value:"+(layoutDirection * consumed));
        return scrolled;
    }

    private void updateLayoutState(int layoutDirection, int requiredSpace) {
        layoutState.mLayoutDirection = layoutDirection;
        int scrollingOffset=0;
        if(layoutDirection == LayoutState.LAYOUT_START){
            final View child = getChildAt(0);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            layoutState.mItemDirection = LayoutState.ITEM_DIRECTION_HEAD;
            layoutState.mCurrentPosition = getPosition(child) + layoutState.mItemDirection;
            layoutState.mOffset = getDecoratedTop(child) - params.topMargin;
            scrollingOffset = -getDecoratedTop(child) - params.topMargin + getPaddingTop();
        } else if (layoutDirection == LayoutState.LAYOUT_END) {
            // get the first child in the direction we are going
            final View child = getChildAt(getChildCount() - 1);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            // the direction in which we are traversing children
            layoutState.mItemDirection = LayoutState.ITEM_DIRECTION_TAIL;
            layoutState.mCurrentPosition = getPosition(child) + layoutState.mItemDirection;//11
            layoutState.mOffset = getDecoratedBottom(child) + params.bottomMargin;//1056
            // calculate how much we can scroll without adding new children (independent of layout)
            scrollingOffset =getDecoratedBottom(child) + params.bottomMargin-getHeight() - getPaddingBottom();//1056 1054- mOrientationHelper.getEndAfterPadding();
        }
        layoutState.mAvailable =requiredSpace- scrollingOffset;
        layoutState.mScrollingOffset = scrollingOffset;
        Log.e(TAG,"offset:"+layoutState.mOffset+" currentPosition = " + layoutState.mCurrentPosition+" scroll:"+scrollingOffset+" available:"+layoutState.mAvailable);
    }

    private void updateLayoutStateToFillEnd(int itemPosition, int offset) {
        layoutState.mOffset=offset;
        layoutState.mAvailable = getHeight() - getPaddingBottom()- offset;
        layoutState.mCurrentPosition = itemPosition;
        layoutState.mLayoutDirection = LayoutState.LAYOUT_END;
        layoutState.mItemDirection = LayoutState.ITEM_DIRECTION_TAIL;
    }

    static class LayoutState {
        final static int LAYOUT_START = -1;
        final static int LAYOUT_END = 1;
        final static int ITEM_DIRECTION_HEAD = -1;
        final static int ITEM_DIRECTION_TAIL = 1;
        final static int SCOLLING_OFFSET_NaN = Integer.MIN_VALUE;
        /**
         * Pixel offset where layout should start
         */
        int mOffset;
        /**
         * Number of pixels that we should fill, in the layout direction.
         */
        int mAvailable;
        /**
         * Current position on the adapter to get the next item.
         */
        int mCurrentPosition;
        /**
         * Defines the direction in which the layout is filled.
         * Should be {@link #LAYOUT_START} or {@link #LAYOUT_END}
         */
        int mLayoutDirection;
        int mScrollingOffset;

        /**
         * Defines the direction in which the data adapter is traversed.
         * Should be {@link #ITEM_DIRECTION_HEAD} or {@link #ITEM_DIRECTION_TAIL}
         */
        int mItemDirection;
        /**
         * @return true if there are more items in the data adapter
         */
        boolean hasMore(RecyclerView.State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        /**
         * Gets the view for the next element that we should layout.
         * Also updates current item index to the next item, based on {@link #mItemDirection}
         *
         * @return The next element that we should layout.
         */
        View next(RecyclerView.Recycler recycler,RecyclerView.State state) {
            final View view = recycler.getViewForPosition(mCurrentPosition);
//            int itemCount = state.getItemCount();
//            if(0<=mCurrentPosition){
//                view = recycler.getViewForPosition(mCurrentPosition%itemCount);
//            } else {
//                view=recycler.getViewForPosition(mCurrentPosition%itemCount+itemCount);
//            }
            mCurrentPosition += mItemDirection;
            return view;
        }
    }
}
