package me.cq.kool.ext

import java.text.DecimalFormat

/**
 * Created by phelps on 2018/2/27 0027.
 */
val Boolean.int: Int
    get() = if(this){
        1
    }else{
        0
    }

val Int.integer2: String
    get() {
        val format = DecimalFormat()
        format.maximumIntegerDigits = Int.MAX_VALUE
        format.minimumIntegerDigits = 2
        return format.format(this)
    }

val Int.secondToHourStr : String
    get() {
        val seconds = (this%60).integer2
        val minutes = (this/60%60).integer2
        val hours = (this/60/60).integer2
        return "$hours:$minutes:$seconds"
    }

val Long.long2: String
    get() {
        val format = DecimalFormat()
        format.maximumIntegerDigits = Int.MAX_VALUE
        format.minimumIntegerDigits = 2
        return format.format(this)
    }

val Long.secondToHourStr : String
    get() {
        val seconds = (this%60).long2
        val minutes = (this/60%60).long2
        val hours = (this/60/60).long2
        return "$hours:$minutes:$seconds"
    }