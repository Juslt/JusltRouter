package com.gx.timer.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.gx.timer.R




class CircleTimerSegmentView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : View(context, attrs, defStyleAttr) {

    /**
     * 270度圆弧进度条,开始角度为-45度
     * 需要绘制两层，下层为半透明白色分段线，上层为白色分段线（需要根据当前进度来）
     * 这里的进度由于是按照每个弧度来分段的，所以path不能直接add整个arc，要一段段的来添加
     * 没个arc间隔1度
     */

    private var mStartAngle = 135f
    private var mSweepAngle = 270f

    private var mBottomCircleColor: Int = R.color.ko_mask_white
    private var mTopCircleColor: Int=R.color.ko_white

    private var mBottomPaint = Paint()
    private var mTopPaint = Paint()
    private var mStrokeWidth = 10f

    private var mIndex = 0
    var mPercent = 0f

    private var mSegmentArray = IntArray(0)

    private var mBottomPath = Path()
    private var mTopPath = Path()

    init {
        mBottomPaint.isAntiAlias = true
        mBottomPaint.color = context.resources.getColor(mBottomCircleColor)
        mBottomPaint.strokeWidth = mStrokeWidth
        mBottomPaint.style = Paint.Style.STROKE
        mBottomPaint.strokeCap = Paint.Cap.ROUND

        mTopPaint.isAntiAlias = true
        mTopPaint.color = context.resources.getColor(mTopCircleColor)
        mTopPaint.strokeWidth = mStrokeWidth
        mTopPaint.style = Paint.Style.STROKE
        mTopPaint.strokeCap = Paint.Cap.ROUND
    }


    override fun onDraw(canvas: Canvas?) {

        val angleRectF = RectF(mStrokeWidth/2,mStrokeWidth/2,measuredWidth.toFloat()-mStrokeWidth/2,measuredHeight.toFloat()-mStrokeWidth/2)

        //计算出来当前的分段path
        val dividerNum = mSegmentArray.size-1
        //减去间隔的弧度，剩下可以被均分的弧度
        val leftAngle = mSweepAngle - dividerNum*3
        val unitAngel = leftAngle/mSegmentArray.sum()
        //安排每段arc的度数,中间需要插入divider,就是3度
        mBottomPath.reset()
        var startAngle = mStartAngle
        mSegmentArray.forEachIndexed { index, i ->
            mBottomPath.addArc(angleRectF,startAngle,i*unitAngel)
            startAngle += i*unitAngel
            if(i != mSegmentArray.size-1){
                //间隔3度
                startAngle += 3f
            }
        }
        //绘制下层圆弧
        canvas?.drawPath(mBottomPath,mBottomPaint)

        //绘制上圆弧
        //只需要绘制已经执行的段数
        mTopPath.reset()
        startAngle = mStartAngle
        if(mIndex==0){
            mTopPath.addArc(angleRectF,startAngle,mSegmentArray[0]*unitAngel*mPercent)
        }else{
            val newArray = mSegmentArray.copyOfRange(0, mIndex+1)
            newArray.forEachIndexed { index, i ->
                if(index==mIndex){
                    mTopPath.addArc(angleRectF,startAngle,i*unitAngel*mPercent)
                    return@forEachIndexed
                }else{
                    mTopPath.addArc(angleRectF,startAngle,i*unitAngel)
                }
                startAngle += i*unitAngel
                if(i != mSegmentArray.size-1){
                    //间隔3度
                    startAngle += 3f
                }
            }
        }

        canvas?.drawPath(mTopPath,mTopPaint)
    }

    fun setSegmentArray(array: IntArray){
        mSegmentArray = array
        postInvalidate()
    }

    fun setCircle(circle: Boolean){
        if(circle){
            mSweepAngle = 360f
            mStartAngle = -90f
        }else{
            mSweepAngle = 270f
            mStartAngle = 135f
        }
        postInvalidate()
    }

    private var animator: ValueAnimator?=null

    fun setProgress(index: Int, percent: Float, anim: Boolean = true){
        mIndex = index

        //使用动画变换过去
        animator?.cancel()

        if(false){
            animator = ValueAnimator.ofFloat(mPercent,percent)
            animator?.addUpdateListener {
                mPercent = it.animatedValue as Float
                postInvalidate()
            }
            animator?.interpolator = LinearInterpolator()
            //暂时用1秒，实际应该用时间差
            animator?.duration = 1000
            animator?.start()
        }else{
            mPercent = percent
            postInvalidate()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}