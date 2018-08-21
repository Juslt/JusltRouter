package me.cq.timer.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import me.cq.kool.ext.dip2px
import me.cq.kool.utils.ResHelper
import me.cq.timer.common.R



class PointProgressView2 @JvmOverloads constructor(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) : ViewGroup(context, attrs, defStyleAttr) {

    private val list = ArrayList<Triple<Float,Float,Int>>()
    private var onSizeChanged = false
    private val pointR = 2.dip2px
    private val pointRM = 4.dip2px
    private val pointRB = 8.dip2px
    private val startColor = ResHelper.getColor(R.color.colorPrimary)
    private val endColor = ResHelper.getColor(R.color.colorPrimaryDark)

    private val count = 14

    private var progress = 0f
    private val progresses = Array(count){
        val unit = 1f/count
        it*unit
    }

    var interval = false

    private val points = Array<PointView?>(count){
        null
    }

    init {
        //count个view，表示count个点的进度
        (0 until count).forEach {
            val point = PointView(context!!)
            point.layoutParams = LayoutParams(pointRB.toInt(),pointRB.toInt())
            points[it] = point
            addView(point)
        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChanged = true
    }

    //重点在layout布局上
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(changed){
            //计算每个点的中心位置
            list.clear()
            var startX = pointRB
            val divider = (measuredWidth-pointRB*2*count)/(count-1)
            for (i in 0 until count) {
                list.add(
                        Triple(startX, pointRB, caculateColor(startColor,endColor,(i+1)/count.toFloat()))
                )
                startX += pointRB * 2 + divider
            }
            //布局每个点
            points.forEachIndexed { index, view ->
                val x = list[index].first-pointRB
                val y = list[index].second-pointRB
                view?.layout(x.toInt(),y.toInt(), (x+pointRB*2).toInt(), (y+pointRB*2).toInt())
                view?.targetColor = list[index].third
            }
        }
    }


    private fun sin(num: Int): Float {
        return Math.sin(num * Math.PI / 180).toFloat()
    }

    private fun cos(num: Int): Float {
        return Math.cos(num * Math.PI / 180).toFloat()
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
        if(progress==0f || progress==1f){
            points.forEach {
                it?.stop()
            }
           return
        }
        val filter = progresses.filter { it < progress }
        filter.forEachIndexed { index, _ ->
            if(index==filter.size-1){
                points[index]?.start(0)
            }else{
                points[index]?.start(1)
            }
        }
    }

}