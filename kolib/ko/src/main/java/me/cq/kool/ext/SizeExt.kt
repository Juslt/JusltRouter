package me.cq.kool.ext

import android.util.TypedValue
import me.cq.kool.Kool

/**
 * Created by phelps on 2017/12/10.
 */
val Int.px2dip : Float
    get() {
        return (this / Kool.getContext().resources.displayMetrics.density)
    }

val Int.dip2px : Float
    get() {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                Kool.getContext().resources.displayMetrics)
    }

val Int.px2sp : Float
    get() {
        return (this / Kool.getContext().resources.displayMetrics.scaledDensity);
    }

val Int.sp2px : Float
    get() {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                this.toFloat(),
                Kool.getContext().resources.displayMetrics)
    }