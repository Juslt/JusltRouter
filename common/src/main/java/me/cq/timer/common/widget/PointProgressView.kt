package me.cq.timer.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import me.cq.kool.ext.dip2px
import me.cq.kool.utils.ResHelper
import me.cq.timer.common.R



class PointProgressView @JvmOverloads constructor(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) : View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    private var path: Path? = null
    private val list = ArrayList<Triple<Float,Float,Int>>()
    private var onSizeChanged = true
    private val pointR = 2.dip2px
    private val pointRM = 4.dip2px
    private val pointRB = 8.dip2px
    private val startColor = ResHelper.getColor(R.color.colorPrimary)
    private val endColor = ResHelper.getColor(R.color.colorPrimaryDark)
    private var progress = 0f

    init {
        path = Path()
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.color = ResHelper.getColor(R.color.colorPrimaryDark_30_per)
        paint!!.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChanged = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLine(canvas,20)
    }

    private fun drawLine(canvas: Canvas, count: Int){
        //计算点位置
        if(onSizeChanged){
            list.clear()
            var startX = pointRB
            val divider = (measuredWidth-pointRB*2*20)/19
            for (i in 0 until count) {
                list.add(
                        Triple(startX, pointRB, caculateColor(startColor,endColor,(i+1)/20f))
                )
                startX += pointRB * 2 + divider
            }
            onSizeChanged = false
        }

        //背景点
        paint!!.color = ResHelper.getColor(R.color.colorPrimaryDark_30_per)
        list.forEach {
            canvas.drawCircle(it.first,it.second,pointR,paint)
        }

        //进度点
        val pointSize = (list.size*progress).toInt()
        if(pointSize>0){
            paint!!.color = ResHelper.getColor(R.color.colorPrimaryDark)
            (0 until  pointSize).forEach{
                paint!!.color = list[it].third
                if(it<pointSize-1){
                    canvas.drawCircle(list[it].first,list[it].second,pointRM,paint)
                }else{
                    canvas.drawCircle(list[it].first,list[it].second,pointRB,paint)
                }
            }
        }
    }


    private fun caculateColor(startColor: Int, endColor: Int, franch: Float): Int {
        val strStartColor = "#" + Integer.toHexString(startColor)
        val strEndColor = "#" + Integer.toHexString(endColor)
        return Color.parseColor(caculateColor(strStartColor, strEndColor, franch))
    }

    private fun caculateColor(startColor: String, endColor: String, franch: Float): String {

        val startAlpha = Integer.parseInt(startColor.substring(1, 3), 16)
        val startRed = Integer.parseInt(startColor.substring(3, 5), 16)
        val startGreen = Integer.parseInt(startColor.substring(5, 7), 16)
        val startBlue = Integer.parseInt(startColor.substring(7), 16)

        val endAlpha = Integer.parseInt(endColor.substring(1, 3), 16)
        val endRed = Integer.parseInt(endColor.substring(3, 5), 16)
        val endGreen = Integer.parseInt(endColor.substring(5, 7), 16)
        val endBlue = Integer.parseInt(endColor.substring(7), 16)

        val currentAlpha = ((endAlpha - startAlpha) * franch + startAlpha).toInt()
        val currentRed = ((endRed - startRed) * franch + startRed).toInt()
        val currentGreen = ((endGreen - startGreen) * franch + startGreen).toInt()
        val currentBlue = ((endBlue - startBlue) * franch + startBlue).toInt()

        return ("#" + getHexString(currentAlpha) + getHexString(currentRed)
                + getHexString(currentGreen) + getHexString(currentBlue))

    }

    /**
     * 将10进制颜色值转换成16进制。
     */
    private fun getHexString(value: Int): String {
        var hexString = Integer.toHexString(value)
        if (hexString.length == 1) {
            hexString = "0$hexString"
        }
        return hexString
    }


    fun setProgress(percent: Float){
        progress = percent
        postInvalidate()
    }

}