package me.cq.kool.ui

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by phelps on 2018/1/27 0027.
 */
fun TextView.setColorRes(res: Int){
    setTextColor(resources.getColor(res))
}

enum class ImageSize{
    FIT_X,
    FIT_Y
}
fun ImageView.setImageResWithFixMode(res: Int, mode: ImageSize=ImageSize.FIT_X){
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources,res,options)
    post {
        val w = measuredWidth
        val h = measuredHeight
        val params = layoutParams
        when(mode){
            ImageSize.FIT_X -> {
                params.height = (w.toFloat()/options.outWidth*options.outHeight).toInt()
            }
            ImageSize.FIT_Y -> {
                params.width = (h.toFloat()/options.outHeight*options.outWidth).toInt()
            }
        }
        layoutParams = params
    }
    setImageResource(res)
}