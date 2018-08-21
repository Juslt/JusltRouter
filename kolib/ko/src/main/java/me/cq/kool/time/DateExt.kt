package me.cq.kool.time

import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Created by phelps on 2018/1/24 0024.
 */

/******************************************opt******************************************/

class MultiTimeUnit(val unit: TimeUnit, val number: Int)

operator fun TimeUnit.times(number: Int) : MultiTimeUnit{
    return MultiTimeUnit(this,number)
}

enum class TimeUnit{
    YEAR,MONTH,WEEK,DAY,HOUR,MINUTE,SECOND,MILLISECOND
}


fun Date.add(field: Int, value: Int) : Date{
    val calendar = this.toCalendar()
    calendar.add(field,value)
    return calendar.time
}

fun Date.toCalendar() : Calendar{
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

operator fun Date.minus(unit: TimeUnit) : Date{
    return plus(MultiTimeUnit(unit,-1))
}

operator fun Date.minus(multiTimeUnit: MultiTimeUnit) : Date{
    return plus(MultiTimeUnit(multiTimeUnit.unit,-multiTimeUnit.number))
}

operator fun Date.plus(unit: TimeUnit) : Date{
    return plus(MultiTimeUnit(unit,1))
}

operator fun Date.plus(multiTimeUnit: MultiTimeUnit) : Date{
    return when(multiTimeUnit.unit){
        TimeUnit.YEAR -> add(Calendar.YEAR,multiTimeUnit.number)
        TimeUnit.MONTH -> add(Calendar.MONTH,multiTimeUnit.number)
        TimeUnit.WEEK -> add(Calendar.WEEK_OF_YEAR,multiTimeUnit.number)
        TimeUnit.DAY -> add(Calendar.DATE,multiTimeUnit.number)
        TimeUnit.HOUR -> add(Calendar.HOUR,multiTimeUnit.number)
        TimeUnit.MINUTE -> add(Calendar.MINUTE,multiTimeUnit.number)
        TimeUnit.SECOND -> add(Calendar.SECOND,multiTimeUnit.number)
        TimeUnit.MILLISECOND -> add(Calendar.MILLISECOND,multiTimeUnit.number)
        else -> throw IllegalArgumentException("不支持的时间类型")
    }
}

/******************************************format******************************************/
private val yyyyMMddHHmmssFormat = SimpleDateFormat("yyyyMMddHHmmss")
private val yyyy_MM_dd_HH_mm_ssFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

val Date.yyyyMMddHHmmss: String
    get() = yyyyMMddHHmmssFormat.format(time)
val Date.yyyy_MM_dd_HH_mm_ss: String
    get() = yyyy_MM_dd_HH_mm_ssFormat.format(time)

/******************************************range to******************************************/
//应该用一个Day的class来代替，这样还可以建立Hour等等 都可以loop
operator fun Date.rangeTo(other: Date) = DateRange(this,other)
class DateRange(val start: Date, val end: Date) : Iterable<Date>{
    override fun iterator(): Iterator<Date> {
        return object : Iterator<Date>{
            var current = start

            override fun hasNext(): Boolean {
                return current <= end
            }

            override fun next(): Date {
                val result = current
                current += TimeUnit.DAY
                return result
            }
        }
    }

}
