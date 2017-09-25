package com.cz.recyclerlibrary.layoutmanager.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.cz.recyclerlibrary.R
import com.cz.recyclerlibrary.DEBUG
import com.cz.recyclerlibrary.layoutmanager.base.CenterLinearLayoutManager
import com.cz.recyclerlibrary.layoutmanager.callback.OnSelectPositionChangedListener

/**
 * Created by cz on 1/17/17.
 */

class WheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var divideDrawable: Drawable? = null
    private var drawableSize: Int = 0
    private var drawablePadding: Int = 0
    private var currentItemPosition: Int = NO_POSITION
    private var valueAnimator: ValueAnimator? = null
    private var listener: OnSelectPositionChangedListener? = null
    private var currentViewHeight: Int = 0
    private var layoutManager=WheelLayoutManager(VERTICAL)

    init {
        setWillNotDraw(false)
        overScrollMode = View.OVER_SCROLL_NEVER

        val a = context.obtainStyledAttributes(attrs, R.styleable.WheelView)
        setDivideDrawable(a.getDrawable(R.styleable.WheelView_wv_divideDrawable))
        setDrawableSize(a.getDimension(R.styleable.WheelView_wv_drawableSize, 0f).toInt())
        setDrawablePadding(a.getDimension(R.styleable.WheelView_wv_drawablePadding, 0f).toInt())
        a.recycle()
    }

    fun setDivideDrawable(drawable: Drawable) {
        this.divideDrawable = drawable
        invalidate()
    }

    fun setDrawableSize(size: Int) {
        this.drawableSize = size
        invalidate()
    }

    fun setDrawablePadding(padding: Int) {
        this.drawablePadding = padding
        invalidate()
    }

    fun setCycle(cycle:Boolean){
        layoutManager.cycle=cycle
    }

    fun setOritation(orientation:Int){
        layoutManager.orientation=orientation
    }

    /**
     * @param layout
     */
    @Deprecated("nothing to do", ReplaceWith("Unit"))
    override fun setLayoutManager(layout: RecyclerView.LayoutManager)=Unit
    /**
     * @param decor
     * *
     */
    @Deprecated("nothing to do")
    override fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
    }

    override fun setAdapter(adapter: Adapter<RecyclerView.ViewHolder>) {
        super.setLayoutManager(layoutManager)
        super.setAdapter(adapter)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (RecyclerView.SCROLL_STATE_IDLE == state) {
            //重置divideDrawable位置,避免快速滑动计算失误
            val layoutManager = layoutManager
            val currentItemPosition = layoutManager.findCurrentItemPosition()
            val currentView = layoutManager.findViewByPosition(currentItemPosition)
            if (null != currentView) {
                currentViewHeight = currentView.measuredHeight
                invalidate()
            }
        }
    }

    val currentCenterPosition: Int
        get() = if (-1 == currentItemPosition) 0 else currentItemPosition

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        val layoutManager = layoutManager
        val newCurrentItemPosition = layoutManager.findCurrentItemPosition()
        //首次进入时currentItemHeight需要初始化,故以-1为入口让其进入
        if (currentItemPosition != newCurrentItemPosition) {
            //item changed
            if(NO_POSITION!=currentCenterPosition){
                currentItemPosition=newCurrentItemPosition
            }
            startWheelDivideChangedAnimator(layoutManager, newCurrentItemPosition, currentItemPosition)
            val findView=layoutManager.findViewByPosition(newCurrentItemPosition)
            listener?.onSelectPositionChanged(findView, newCurrentItemPosition,currentItemPosition)
            currentItemPosition = newCurrentItemPosition
        }
    }

    private fun startWheelDivideChangedAnimator(layoutManager: WheelLayoutManager, current: Int, last: Int) {
        if (null != valueAnimator) {
            valueAnimator?.removeAllUpdateListeners()
            valueAnimator?.cancel()
        }
        //current24 last:23
        val currentView = layoutManager.findViewByPosition(current)
        val lastView = layoutManager.findViewByPosition(last)
        if (null != currentView && null != lastView) {
            valueAnimator = ValueAnimator.ofInt(lastView.measuredHeight, currentView.measuredHeight)
            valueAnimator?.addUpdateListener { animation ->
                currentViewHeight = animation.animatedValue as Int
                invalidate()
            }
            valueAnimator?.start()
        }
    }

    val itemCount: Int
        get() = if (null == adapter) 0 else adapter.itemCount

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (0 < childCount && null != divideDrawable) {
            val centerY = height / 2
            //top divide
            divideDrawable?.setBounds(drawablePadding, centerY - currentViewHeight / 2 - drawableSize, width - drawablePadding, centerY - currentViewHeight / 2 + drawableSize)
            divideDrawable?.draw(canvas)
            //bottom divide
            divideDrawable?.setBounds(drawablePadding, centerY + currentViewHeight / 2 - drawableSize, width - drawablePadding, centerY + currentViewHeight / 2 + drawableSize)
            divideDrawable?.draw(canvas)
        }
        if (DEBUG) {
            val width = width
            val height = height
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.RED
            paint.strokeWidth = 8f
            canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)
            canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        }
    }

    fun setOnSelectPositionChangedListener(listener: OnSelectPositionChangedListener) {
        this.layoutManager.setOnSelectPositionChangedListener(listener)
    }

    fun onSelectPositionChanged(callback:(View?, Int, Int)->Unit){
        setOnSelectPositionChangedListener(object :OnSelectPositionChangedListener{
            override fun onSelectPositionChanged(view: View?, position: Int, lastPosition: Int) {
                callback(view,position,lastPosition)
            }
        })
    }

}
