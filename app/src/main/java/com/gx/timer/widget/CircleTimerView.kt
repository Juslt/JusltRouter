package com.gx.timer.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.gx.timer.R
import android.graphics.DashPathEffect
import java.lang.reflect.Array.getLength
import android.graphics.PathMeasure
import android.view.animation.LinearInterpolator


class CircleTimerView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : View(context, attrs, defStyleAttr) {

    /**
     * 270度圆弧进度条,开始角度为-45度
     * 需要绘制两层，下层为白色虚线，上层为粉色圆头实线
     */

    private var mStartAngle = 135f
    private var mSweepAngle = 270f

    var mBottomCircleColor: Int = R.color.ko_mask_white
    var mTopCircleColor: Int=R.color.colorAccent

    var mBottomPaint = Paint()
    var mTopPaint = Paint()
    var mStrokeWidth = 20f

    var mCurrentPercentage = 0f

    init {
        mBottomPaint.isAntiAlias = true
        mBottomPaint.color = context.resources.getColor(mBottomCircleColor)
        mBottomPaint.strokeWidth = mStrokeWidth
        mBottomPaint.style = Paint.Style.STROKE

        mTopPaint.isAntiAlias = true
        mTopPaint.color = context.resources.getColor(mTopCircleColor)
        mTopPaint.strokeWidth = mStrokeWidth
        mTopPaint.style = Paint.Style.STROKE
        mTopPaint.strokeCap = Paint.Cap.ROUND
    }


    override fun onDraw(canvas: Canvas?) {

        val angleRectF = RectF(mStrokeWidth/2,mStrokeWidth/2,measuredWidth.toFloat()-mStrokeWidth/2,measuredHeight.toFloat()-mStrokeWidth/2)

        //绘制下层圆弧
        val pathBottom = Path()
        pathBottom.addArc(angleRectF,mStartAngle,mSweepAngle)

        val pathMeasure = PathMeasure(pathBottom, false)
        val length = pathMeasure.length
        val step = length / 100
        val dashPathEffect = DashPathEffect(floatArrayOf(step / 3, step), 0f)
        mBottomPaint.pathEffect = dashPathEffect

        canvas?.drawPath(pathBottom,mBottomPaint)

        //绘制上圆弧,使用
        val pathTop = Path()
        pathTop.addArc(angleRectF,mStartAngle,mSweepAngle*mCurrentPercentage)
        canvas?.drawPath(pathTop,mTopPaint)
    }

    fun setProgress(percentage: Float){
        //使用动画变换过去
        mCurrentPercentage = percentage
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

}