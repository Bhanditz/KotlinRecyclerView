package cz.refreshlayout.library.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cz.refreshlayout.library.R
import android.graphics.CornerPathEffect
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator


/**
 * Created by cz on 2017/8/2.
 */
class WalletView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    private val CONST_RADIUS = dp2px(1.2f)
    private val CONST_LEFT_RECT_W = dp2px(12f)
    private val CONST_RIGHT_RECT_W = dp2px(18f)
    private val MIN_LEFT_RECT_W = dp2px(3.3f)
    private val MAX_RIGHT_RECT_W = CONST_RIGHT_RECT_W + dp2px(6.7f)

    private var maxLeftRectWidth: Float = 0f
    private var leftRectWidth: Float = 0f
    private var rightRectWidth: Float = 0f
    private var leftRectGrowMode: Boolean = false

    private val paint= Paint(Paint.ANTI_ALIAS_FLAG).apply { style=Paint.Style.STROKE }
    private val innerPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { style=Paint.Style.STROKE }
    private val pathPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { style=Paint.Style.STROKE }
    private val tickPaint=Paint(Paint.ANTI_ALIAS_FLAG)
    private var strokeWidth:Float=0f
    private var maxStrokeWidth:Float=0f
    private var circleRadius:Float=0f
    private var innerPadding:Float=0f
    private var interval:Float=0f
    private var animationFraction:Float=0f
    private val pathMeasure =PathMeasure()
    private val pos= floatArrayOf(0f,0f)
    private val rectPath = Path()
    private val dividerPath = Path()
    private val rectF=RectF()
    private val rectF1=RectF()

    private var cameraMatrix:Matrix=Matrix()
    private val camera=Camera()

    private var animatorSet:AnimatorSet?=null
    private var rotateAnimation:ValueAnimator?=null
    private var tickAnimation:ValueAnimator?=null
    constructor(context: Context):this(context,null,R.attr.walletView)
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,R.attr.walletView)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WalletView,defStyleAttr,R.style.WalletView).apply {
            setRadius(getDimension(R.styleable.WalletView_wv_radius,0f))
            setStrokeWidth(getDimension(R.styleable.WalletView_wv_strokeWidth,0f))
            setMaxStrokeWidth(getDimension(R.styleable.WalletView_wv_maxStrokeWidth,0f))
            setColor(getColor(R.styleable.WalletView_wv_color, Color.TRANSPARENT))
            setInnerStrokeWidth(getDimension(R.styleable.WalletView_wv_innerStrokeWidth,0f))
            setInnerPadding(getDimension(R.styleable.WalletView_wv_innerPadding,0f))
            setInnerColor(getColor(R.styleable.WalletView_wv_innerColor, Color.TRANSPARENT))
            setInterval(getDimension(R.styleable.WalletView_wv_interval,0f))
            recycle()
        }
    }

    fun dp2px(value: Float): Float=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,resources.displayMetrics)

    fun setRadius(radius: Float) {
        circleRadius=radius
        requestLayout()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth=strokeWidth
        invalidate()
    }

    fun setMaxStrokeWidth(strokeWidth: Float){
        this.maxStrokeWidth=strokeWidth
        invalidate()
    }

    fun setColor(color: Int) {
        paint.color=color
        invalidate()
    }

    fun setInnerStrokeWidth(strokeWidth: Float) {
        pathPaint.strokeWidth=strokeWidth
        innerPaint.strokeWidth=strokeWidth
        tickPaint.strokeWidth=strokeWidth
        invalidate()
    }

    fun setInnerPadding(padding: Float) {
        innerPadding=padding
        invalidate()
    }

    fun setInnerColor(color: Int) {
        pathPaint.color=color
        innerPaint.color=color
        tickPaint.color=color
        invalidate()
    }

    fun setInterval(padding:Float){
        this.interval=padding
        invalidate()
    }

    /**
     * 设置动画速率
     */
    fun setAnimationFraction(fraction:Float){
        this.animationFraction=fraction
        invalidate()
    }


    fun start(){
        if(null!=animatorSet) return
        animatorSet?.removeAllListeners()
        animatorSet?.cancel()
        //启动三个动画,一个使书面依次出来,另一个使背包旋转
        val valueAnimator1=ValueAnimator.ofFloat(1f).apply {
            addUpdateListener { invalidate() }
        }
        val valueAnimator2=ValueAnimator.ofFloat(1f).apply {
            duration=100
            addUpdateListener { invalidate() }
        }
        val valueAnimator3=ValueAnimator.ofFloat(1f).apply {
            duration=100
            addUpdateListener { invalidate() }
        }
        animatorSet=AnimatorSet().apply {
            playSequentially(valueAnimator1,valueAnimator2,valueAnimator3)
            start()
        }

    }

    fun stop(){
        leftRectWidth = 0f
        rightRectWidth = 0f
        animatorSet?.let {
            it.childAnimations.forEach {
                //update listener 必须手动移除,否则无限动画,将一直执行下去
                if(it is ValueAnimator){
                    it.removeAllUpdateListeners()
                    it.removeAllListeners()
                }
            }
        }
        animatorSet?.removeAllListeners()
        animatorSet?.cancel()
        animatorSet=null
    }

    fun startRotateAnimation(){
        rotateAnimation=ValueAnimator.ofFloat(1f).apply {
            duration=1000
            addUpdateListener { invalidate() }
            interpolator=DecelerateInterpolator()
            repeatCount=ValueAnimator.INFINITE
            repeatMode=ValueAnimator.RESTART
            start()
        }
    }

    fun stopRotateAnimation(){
        animatorSet?.removeAllListeners()
        rotateAnimation?.cancel()
        rotateAnimation=null
    }

    fun startTickAnim(action:(()->Unit)?=null) {
        //结束旋转动画
        stop()
        stopRotateAnimation()
        leftRectWidth = 0f
        rightRectWidth = 0f
        tickAnimation=ValueAnimator.ofFloat(1f).apply {
            duration=750
            startDelay=100
            addUpdateListener {
                val animatedFraction = it.animatedFraction
                if (0.54 < animatedFraction && 0.7 >= animatedFraction) {
                    leftRectGrowMode = true
                    leftRectWidth = maxLeftRectWidth * ((animatedFraction - 0.54f) / 0.16f)
                    if (0.65 < animatedFraction) {
                        rightRectWidth = MAX_RIGHT_RECT_W * ((animatedFraction - 0.65f) / 0.19f)
                    }
                } else if (0.7 < animatedFraction && 0.84 >= animatedFraction) {
                    leftRectGrowMode = false
                    leftRectWidth = maxLeftRectWidth * (1 - (animatedFraction - 0.7f) / 0.14f)
                    leftRectWidth = if (leftRectWidth < MIN_LEFT_RECT_W) MIN_LEFT_RECT_W else leftRectWidth
                    rightRectWidth = MAX_RIGHT_RECT_W * ((animatedFraction - 0.65f) / 0.19f)
                } else if (0.84 < animatedFraction && 1 >= animatedFraction) {
                    leftRectGrowMode = false
                    leftRectWidth = MIN_LEFT_RECT_W + (CONST_LEFT_RECT_W - MIN_LEFT_RECT_W) * ((animatedFraction - 0.84f) / 0.16f)
                    rightRectWidth = CONST_RIGHT_RECT_W + (MAX_RIGHT_RECT_W - CONST_RIGHT_RECT_W) * (1 - (animatedFraction - 0.84f) / 0.16f)
                }
                invalidate()
            }
            addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    action?.invoke()
                }
            })
            interpolator=DecelerateInterpolator()
            start()
        }
    }

    fun stopTickAnimation(){
        tickAnimation?.removeAllListeners()
        tickAnimation?.cancel()
        tickAnimation=null
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension((paddingLeft+circleRadius*2+paddingRight).toInt(),
                (paddingTop+circleRadius*2+paddingBottom).toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.left=paddingLeft+innerPadding
        rectF.top= paddingTop+innerPadding
        rectF.right= width-paddingRight-innerPadding
        rectF.bottom= height-paddingBottom-innerPadding

        rectPath.reset()
        rectPath.addRoundRect(rectF,0f,0f,Path.Direction.CCW)
        //中间弯曲线
        pathPaint.pathEffect=CornerPathEffect(interval)
        dividerPath.moveTo(rectF.left,rectF.centerY())
        dividerPath.lineTo(rectF.centerX(),rectF.centerY()+rectF.height()/4)
        dividerPath.lineTo(rectF.right,rectF.centerY())
        dividerPath.lineTo(rectF.centerX(), rectF.centerY()+ rectF.height()/4)
        dividerPath.lineTo(rectF.left, rectF.centerY())
        pathMeasure.setPath(dividerPath,false)
        //计算位置interval的pos位置
        pathMeasure.getPosTan(interval,pos,null)

        rectF1.left=paddingLeft+innerPadding+interval
        rectF1.top= paddingTop+innerPadding+interval
        rectF1.right= width-paddingRight-innerPadding-interval
        rectF1.bottom= height-paddingBottom-innerPadding-interval
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //圆随速率变大,并往下移动
        paint.strokeWidth=strokeWidth+(maxStrokeWidth-strokeWidth)*(1f-animationFraction)
        canvas.drawCircle(width/2f, height/2f,
                (circleRadius-paint.strokeWidth/2)*animationFraction,paint)
        //画钱包...
        if (1f <= animationFraction&&0<width&&0<height) {
            drawWallet(canvas)
        }
        //画成功标记
//        drawTick(canvas)
    }

    private fun drawWallet(canvas: Canvas) {
        //中间绘图逻辑
        val fraction1 = (animatorSet?.childAnimations?.get(0) as? ValueAnimator)?.animatedFraction ?: 0f
        val fraction2 = (animatorSet?.childAnimations?.get(1) as? ValueAnimator)?.animatedFraction ?: 0f
        val fraction3 = (animatorSet?.childAnimations?.get(2) as? ValueAnimator)?.animatedFraction ?: 0f
        val fraction4 = rotateAnimation?.animatedFraction ?: 0f

        pathPaint.alpha = (0xFF * fraction1).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)

        val bitmapCanvas = Canvas(bitmap)

        bitmapCanvas.drawPath(rectPath, pathPaint)
        bitmapCanvas.drawPath(dividerPath, pathPaint)

        //绘三层,带速率,且最后一组形成无限向上移动
        val path = Path()
        arrayOf(1f, fraction2, fraction3).forEachIndexed { index, fraction ->
            path.moveTo(rectF1.left, pos[1])
            path.lineTo(rectF1.left, rectF1.top + interval * 2 - (interval * index) * fraction)
            path.lineTo(rectF1.right, rectF1.top + interval * 2 - (interval * index) * fraction)
            path.lineTo(rectF1.right, pos[1])
        }
        bitmapCanvas.drawPath(path, pathPaint)
        camera.save()
        //绕X轴翻转
        camera.rotateY(360f * fraction4)
        //设置camera作用矩阵
        camera.getMatrix(cameraMatrix)
        camera.restore()
        //设置翻转中心点
        cameraMatrix.preTranslate(-width / 2f, -height / 2f)
        cameraMatrix.postTranslate(width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, cameraMatrix, null)
    }

    private fun drawTick(canvas: Canvas){
        var width = width.toFloat()
        var height = height.toFloat()
        val strokeWidth = tickPaint.strokeWidth
        // rotate canvas first
        canvas.rotate(45f, width / 2, height / 2)
        width /= 1.20f
        height /= 1.15f
        maxLeftRectWidth = (width + CONST_LEFT_RECT_W) / 2 + strokeWidth - 1

        val leftRect = RectF()
        if (leftRectGrowMode) {
            leftRect.left = 0f
            leftRect.right = leftRect.left + leftRectWidth
            leftRect.top = (height + CONST_RIGHT_RECT_W) / 2
            leftRect.bottom = leftRect.top + strokeWidth
        } else {
            leftRect.right = (width + CONST_LEFT_RECT_W) / 2 + strokeWidth - 1
            leftRect.left = leftRect.right - leftRectWidth
            leftRect.top = (height + CONST_RIGHT_RECT_W) / 2
            leftRect.bottom = leftRect.top + strokeWidth
        }
        canvas.drawRoundRect(leftRect, CONST_RADIUS, CONST_RADIUS, tickPaint)
        val rightRect = RectF()
        rightRect.bottom = (height + CONST_RIGHT_RECT_W) / 2 + strokeWidth - 1
        rightRect.left = (width + CONST_LEFT_RECT_W) / 2
        rightRect.right = rightRect.left + strokeWidth
        rightRect.top = rightRect.bottom - rightRectWidth
        canvas.drawRoundRect(rightRect, CONST_RADIUS, CONST_RADIUS, tickPaint)
    }
}