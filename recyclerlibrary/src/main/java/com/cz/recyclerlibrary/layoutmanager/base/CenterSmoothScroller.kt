package com.cz.recyclerlibrary.layoutmanager.base

import android.content.Context
import android.graphics.PointF
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.cz.sample.ui.layoutmanager.BaseLinearLayoutManager

/**
 * [RecyclerView.SmoothScroller] implementation which uses a [LinearInterpolator] until
 * the target position becomes a child of the RecyclerView and then uses a
 * [DecelerateInterpolator] to slowly approach to target position.
 *
 *
 * If the [RecyclerView.LayoutManager] you are using does not implement the
 * [ScrollVectorProvider] interface, then you must override the
 * [.computeScrollVectorForPosition] method. All the LayoutManagers bundled with
 * the support library implement this interface.
 */
open class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {

    override fun onTargetFound(targetView: View, state: RecyclerView.State?, action: RecyclerView.SmoothScroller.Action) {
        val layoutManager = layoutManager as CenterLinearLayoutManager
        var dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference)
        var dy = calculateDyToMakeVisible(targetView, verticalSnapPreference)
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
        val time = calculateTimeForDeceleration(distance) * 10
        val offsetX = (layoutManager.width - targetView.measuredWidth) / 2
        val offsetY = (layoutManager.height - targetView.measuredHeight) / 2
        if (null != mTargetVector) {
            if(layoutManager.orientation==BaseLinearLayoutManager.VERTICAL){
                if (-1f == mTargetVector.y) {
                    //向上
                    dy += offsetY
                } else if (1f == mTargetVector.y) {
                    //向下
                    dy -= offsetY
                }
            } else {
                if (-1f == mTargetVector.x) {
                    //向上
                    dx += offsetX
                } else if (1f == mTargetVector.x) {
                    //向下
                    dx -= offsetX
                }
            }
        }
        if (time > 0) {
            action.update(-dx, -dy, time, mDecelerateInterpolator)
        }
    }

    override fun updateActionForInterimTarget(action: RecyclerView.SmoothScroller.Action) {
        // find an interim target position
        val scrollVector = computeScrollVectorForPosition(targetPosition)
        if (scrollVector == null || scrollVector.x == 0f && scrollVector.y == 0f) {
            val target = targetPosition
            action.jumpTo(target)
            stop()
            return
        }
        normalize(scrollVector)
        mTargetVector = scrollVector

        mInterimTargetDx = (TARGET_SEEK_SCROLL_DISTANCE_PX * scrollVector.x).toInt()
        mInterimTargetDy = (TARGET_SEEK_SCROLL_DISTANCE_PX * scrollVector.y).toInt()
        // 在原time 基础上,时间*4
        val time = calculateTimeForScrolling(TARGET_SEEK_SCROLL_DISTANCE_PX) * 6
        // To avoid UI hiccups, trigger a smooth scroll to a distance little further than the
        // interim target. Since we track the distance travelled in onSeekTargetStep callback, it
        // won't actually scroll more than what we need.
        action.update((mInterimTargetDx * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), (mInterimTargetDy * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), (time * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(), mLinearInterpolator)
    }

    companion object {
        private val TARGET_SEEK_SCROLL_DISTANCE_PX = 10000
        private val TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f
    }
}
