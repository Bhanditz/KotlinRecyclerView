package com.ldzs.recyclerlibrary.footer

import android.content.Context
import android.support.annotation.IntDef
import android.view.View

import com.ldzs.recyclerlibrary.R


/**
 * Created by czz on 2016/8/17.
 */
class RefreshFrameFooter(context: Context) {
    companion object {
        const val FRAME_CLICK = 0
        const val FRAME_LOAD = 1
        const val FRAME_ERROR = 2
        const val FRAME_DONE = 3
    }
    @IntDef(value = *longArrayOf(FRAME_CLICK.toLong(), FRAME_LOAD.toLong(), FRAME_ERROR.toLong(), FRAME_DONE.toLong()))
    annotation class RefreshState

    private val frameGroup: Array<View>
    val footerView: View
    private var refreshState = FRAME_CLICK
    private var clickListener: View.OnClickListener? = null
    private var lastFrame: View? = null

    init {
        this.footerView = FrameFooterView(context)
        frameGroup = arrayOf<View>(footerView.findViewById(R.id.refresh_click_view),
                this.footerView.findViewById(R.id.refresh_loading_layout),
                this.footerView.findViewById(R.id.refresh_error_layout),
                this.footerView.findViewById(R.id.refresh_complete_layout))
        frameGroup[FRAME_ERROR].findViewById(R.id.tv_error_try).setOnClickListener { v ->
            if (null != clickListener) {
                setRefreshState(FRAME_LOAD)
                //delayed three hundred millisecond show progress view
                v.postDelayed({ clickListener!!.onClick(v) }, 300)
            }
        }
        setRefreshState(FRAME_LOAD)
    }

    fun refreshComplete() {
        setRefreshState(FRAME_DONE)
    }

    val isRefreshing: Boolean
        get() = FRAME_LOAD == refreshState

    val isRefreshDone: Boolean
        get() = FRAME_DONE == refreshState

    /**
     * set footer refresh state

     * @param state
     */
    fun setRefreshState(@RefreshState state: Int) {
        if (refreshState == state) return
        footerView.post{
            frameGroup[state].visibility = View.VISIBLE
            lastFrame = frameGroup[refreshState]
            if (null != lastFrame) {
                lastFrame!!.visibility = View.GONE
            }
            refreshState = state
        }
    }


    private fun isCurrentState(state: Int): Boolean {
        return refreshState == state
    }

    fun setOnFootRetryListener(listener: View.OnClickListener) {
        this.clickListener = listener
    }

}
