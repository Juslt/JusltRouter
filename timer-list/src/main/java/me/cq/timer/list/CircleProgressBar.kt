package me.cq.timer.list

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import me.cq.kool.ext.dip2px
import me.cq.kool.utils.ResHelper

class CircleProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val paintSecondary = Paint()
    private val strokeWidth = 4.dip2px
    private var sweepGradient: SweepGradient?=null
    private var progress = 0f

    init {
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
//        paint.color = ResHelper.getColor(R.color.colorPrimaryDark)

        paintSecondary.isAntiAlias = true
        paintSecondary.strokeWidth = strokeWidth
        paintSecondary.style = Paint.Style.STROKE
        paintSecondary.strokeCap = Paint.Cap.ROUND
        paintSecondary.color = ResHelper.getColor(R.color.colorPrimaryDark_30_per)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()

        //画背景
        paint.shader = null
        canvas.drawCircle(w/2,h/2,w/2-strokeWidth/2,paintSecondary)

        if(progress==0f){
            return
        }
        //画前景
        if(sweepGradient==null){
            sweepGradient = SweepGradient(w/2,h/2,ResHelper.getColor(R.color.colorPrimary),ResHelper.getColor(R.color.colorPrimaryDark))
        }

        paint.shader = sweepGradient
        val rectf = RectF(strokeWidth/2,strokeWidth/2,w-strokeWidth/2,h-strokeWidth/2)
        canvas.save()
        canvas.rotate(-90f,w/2,h/2)
        canvas.drawArc(rectf,5f,360*progress,false,paint)
        canvas.restore()
    }

    fun setProgress(p: Float){
        progress = p/100
        postInvalidate()
    }
}