package com.ldzs.recyclerlibrary.footer

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.ldzs.recyclerlibrary.BuildConfig
import com.ldzs.recyclerlibrary.R

/**
 * Created by Administrator on 2016/8/20.
 */
class FrameFooterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.footerStyle) : RelativeLayout(context, attrs, defStyleAttr) {
    private val clickView: TextView
    private val loadLayout: LinearLayout
    private val refreshHintView: TextView
    private val errorLayout: LinearLayout
    private val errorText: TextView
    private val errorRetry: TextView
    private val completeText: TextView

    init {
        View.inflate(context, R.layout.list_footer, this)
        clickView = findViewById(R.id.refresh_click_view) as TextView
        loadLayout = findViewById(R.id.refresh_loading_layout) as LinearLayout
        refreshHintView = findViewById(R.id.refresh_hint_info) as TextView
        errorLayout = findViewById(R.id.refresh_error_layout) as LinearLayout
        errorText = findViewById(R.id.refresh_error_text) as TextView
        errorRetry = findViewById(R.id.tv_error_try) as TextView
        completeText = findViewById(R.id.refresh_complete_layout) as TextView

        val a = context.obtainStyledAttributes(attrs, R.styleable.FrameFooterView, defStyleAttr, R.style.FrameFooterView)
        setFooterHeight(a.getDimension(R.styleable.FrameFooterView_footer_footerHeight, 0f))
        setFooterClickTextHint(a.getString(R.styleable.FrameFooterView_footer_clickTextHint))
        setFooterTextSize(a.getDimensionPixelSize(R.styleable.FrameFooterView_footer_textSize, 0))
        setFooterTextColor(a.getColor(R.styleable.FrameFooterView_footer_textColor, Color.TRANSPARENT))
        setFooterErrorHint(a.getString(R.styleable.FrameFooterView_footer_errorHint))
        setFooterComplete(a.getString(R.styleable.FrameFooterView_footer_complete))
        setFooterRetryItemSelector(a.getDrawable(R.styleable.FrameFooterView_footer_retryItemSelector))
        setFooterRetry(a.getString(R.styleable.FrameFooterView_footer_retry))
        setFooterLoad(a.getString(R.styleable.FrameFooterView_footer_load))
        a.recycle()
    }


    fun setFooterHeight(height: Float) {
        val layoutParams = layoutParams
        if (null != layoutParams) {
            layoutParams.height = height.toInt()
            requestLayout()
        }
    }

    fun setFooterClickTextHint(hint: String) {
        clickView.text = hint
    }

    fun setFooterTextSize(textSize: Int) {
        clickView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        refreshHintView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        errorText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        errorRetry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        completeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setFooterTextColor(color: Int) {
        clickView.setTextColor(color)
        refreshHintView.setTextColor(color)
        errorText.setTextColor(color)
        errorRetry.setTextColor(color)
        completeText.setTextColor(color)
    }

    fun setFooterLoad(load: String) {
        refreshHintView.text = load
    }

    fun setFooterErrorHint(hint: String) {
        errorText.text = hint
    }

    fun setFooterComplete(complete: String) {
        completeText.text = complete
    }

    fun setFooterRetryItemSelector(drawable: Drawable?) {
        if (null != drawable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                errorRetry.setBackgroundDrawable(drawable)
            } else {
                errorRetry.background = drawable
            }
        }
    }

    fun setFooterRetry(retry: String) {
        errorRetry.text = retry
    }

}
