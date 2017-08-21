package cz.refreshlayout.library.header

import android.content.Context
import android.view.View
import android.view.ViewGroup
import cz.refreshlayout.library.RefreshState
import cz.refreshlayout.library.debugLog
import cz.refreshlayout.library.widget.MaterialProgressDrawable
import cz.refreshlayout.library.widget.MaterialProgressView

/**
 * Created by cz on 2017/7/28.
 */
class MaterialDesignHeader(context: Context):BaseRefreshHeader(){

    private var materialProgressView= MaterialProgressView(context)
    override val headerView: View
        get() = materialProgressView
    init {
        materialProgressView.setPadding(0, 60, 0, 60)
        materialProgressView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onScrollOffset(fraction: Float) {
        val drawable = materialProgressView.getMaterialDrawable()
        drawable.alpha = (255 * fraction).toInt()
        val strokeStart = fraction * .8f
        drawable.setStartEndTrim(0f, Math.min(0.8f, strokeStart))
        drawable.setArrowScale(Math.min(1f, fraction))

        val rotation = (-0.25f + .4f * fraction + fraction * 2) * .5f
        drawable.setProgressRotation(rotation)
    }

    override fun onRefreshStateChange(state: RefreshState) {
        debugLog("onRefreshStateChange:$state")
        val drawable = materialProgressView.getMaterialDrawable()
        when (state) {
            RefreshState.START_PULL -> {
                materialProgressView.visibility = View.VISIBLE
                stopArrowDrawable(drawable)
            }
            RefreshState.REFRESHING -> {
                materialProgressView.visibility = View.VISIBLE
                if (!drawable.isRunning) {
                    drawable.showArrow(false)
                    drawable.alpha = 0xFF
                    drawable.start()
                }
            }
            RefreshState.NONE -> {
                materialProgressView.visibility = View.GONE
                stopArrowDrawable(drawable)
            }
        }
    }

    private fun stopArrowDrawable(drawable: MaterialProgressDrawable) {
        debugLog("stopArrowDrawable")
        drawable.stop()
        drawable.setArrowScale(1f)
        drawable.showArrow(true)
    }

}