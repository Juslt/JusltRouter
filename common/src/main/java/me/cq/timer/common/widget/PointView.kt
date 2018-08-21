package me.cq.timer.common.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.util.AttributeSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import me.cq.kool.ext.dip2px
import me.cq.timer.common.R
import me.cq.kool.utils.ResHelper

class PointView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : View(context, attrs, defStyleAttr) {

    private val pointR = 2.dip2px
    private val pointRM = 4.dip2px
    private val pointRB = 8.dip2px
    private val bgColor = ResHelper.getColor(R.color.colorPrimaryDark_30_per)
    private val startColor = ResHelper.getColor(R.color.colorPrimary)
    private val endColor = ResHelper.getColor(R.color.colorPrimaryDark)
    private val paint = Paint()

    public var color = bgColor
    public var targetColor = endColor

    init {
        paint.isAntiAlias = true
        paint.color = bgColor
        paint.style = Paint.Style.FILL
    }

    //0 初始化，背景点
    //1 从背景点放最大的点，
    //2 从最大到中间大的点
    private var state1 = false
    private var state2 = false

    fun start(state: Int){
        post {
            when(state){
                0 -> {
                    if(state1){
                        return@post
                    }
                    color = targetColor
                    invalidate()
                    state1 = true
                    val animation = ScaleAnimation(1f,4f,1f,4f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
                    animation.duration = 500
                    animation.fillAfter = true
                    startAnimation(animation)
                }
                1 -> {
                    if(state2){
                        return@post
                    }
                    color = targetColor
                    invalidate()
                    state2 = true
                    clearAnimation()
                    val animation = ScaleAnimation(4f,2f,4f,2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
                    animation.duration = 250
                    animation.fillAfter = true
                    startAnimation(animation)
                }
            }
        }
    }

    fun stop(){
        post {
            clearAnimation()
            state1 = false
            state2 = false
            color = bgColor
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = color
        canvas.drawCircle(measuredWidth/2f+pointRB,measuredHeight/2f+pointRB,pointR,paint)
    }

}