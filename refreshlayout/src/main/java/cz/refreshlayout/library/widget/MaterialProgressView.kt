package cz.refreshlayout.library.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation

/**
 * Created by Administrator on 2016/8/16.
 */
class MaterialProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val materialDrawable: MaterialProgressDrawable = MaterialProgressDrawable(context, this)
    private val scale = 1f

    init {
        materialDrawable.setBackgroundColor(Color.WHITE)
        materialDrawable.callback = this
    }

    fun setDrawableAlpha(alpha: Int) {
        materialDrawable.alpha = alpha
        invalidate()
    }

    override fun invalidate() {
        if (hasWindowFocus()) super.invalidate()
    }

    fun getMaterialDrawable():MaterialProgressDrawable=materialDrawable

    override fun invalidateDrawable(drawable: Drawable) {
        if (drawable == materialDrawable) {
            invalidate()
        } else {
            super.invalidateDrawable(drawable)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = materialDrawable.intrinsicWidth + paddingLeft + paddingRight
        val measuredHeight = materialDrawable.intrinsicHeight + paddingTop + paddingBottom
        setMeasuredDimension(measuredWidth,measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val size = materialDrawable.intrinsicHeight
        materialDrawable.setBounds(0, 0, size, size)
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        val rect = materialDrawable.bounds
        val l = paddingLeft + (measuredWidth - materialDrawable.intrinsicWidth) / 2
        canvas.translate(l.toFloat(), paddingTop.toFloat())
        canvas.scale(scale, scale, rect.exactCenterX(), rect.exactCenterY())
        materialDrawable.draw(canvas)
        canvas.restoreToCount(saveCount)
    }
}
