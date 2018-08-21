package me.cq.timer.common

import android.content.ContentValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.cq.timer.common.controller.TimerState
import me.cq.timer.common.database.DBManager
import java.io.Serializable

/**
 * Created by phelps on 2018/4/23 0023.
 */

open class TimerVo : Serializable{
    var _id: Int?= null
    var id: String = ""
    var time: Long = 0
    var name: String = ""
    //所有数据共享一个order
    var order: Int = -1
//    var currentIndex: Int = 0
    //0 正常， 1 编辑， 2，单个编辑
    var mode: Int = 0

    override fun equals(other: Any?): Boolean {
        return if(other !is TimerVo){
            false
        }else{
            id == other.id
        }
    }

    override fun hashCode(): Int {
        return ("time_vo_$id").hashCode()
    }

    fun getSecondsByIndexLimit(limit: Int) : Int{
        return when(this){
            is MultiTimerVo -> {
                var second = 0
                this.multiSubTimerVoList.forEachIndexed { index, multiSubTimerVo ->
                    if(index<=limit){
                        second += multiSubTimerVo.seconds
                    }
                }
                second
            }
            is IntervalTimerVo -> {
                var second = 0
                this.list.forEachIndexed { index, seconds ->
                    if(index<=limit){
                        second += seconds
                    }
                }
                second
            }
            else -> 0
        }
    }

    val totalSeconds by lazy{
        when(this){
            is SimpleTimerVo -> this.seconds.toLong()
            is MultiTimerVo -> {
                this.multiSubTimerVoList.sumBy { it.seconds }.toLong()
            }
            is IntervalTimerVo -> {
                (this.secondsPrepare+ (this.secondsTraining+this.secondsRest)*group.toLong())*repeat
            }
            else -> 0L
        }
    }

    val totalIndex by lazy {
        when(this){
            is MultiTimerVo -> {
                this.multiSubTimerVoList.size
            }
            is IntervalTimerVo -> {
                1 + 2*repeat
            }
            else -> 1 //简单计时器只有1
        }
    }
}

data class SimpleTimerVo(
        var seconds: Int=0,
        var tone: String= simpleRings[0].name
) : TimerVo(),Serializable{

    fun toCV() : ContentValues{
        val contentValues = ContentValues()
        contentValues.put("name",name)
        contentValues.put("seconds",seconds)
        contentValues.put("tone",tone)
        contentValues.put("time",System.currentTimeMillis())
        if(order==-1){
            //更新为数据库的最后一个
            order = DBManager.queryMAXOrder()+1
        }
        contentValues.put("_order",order)
        return contentValues
    }

    companion object {
        fun fromCV(contentValues: ContentValues) : SimpleTimerVo{
            val simpleTimerVo = SimpleTimerVo(
                    contentValues.getAsInteger("seconds"),
                    contentValues.getAsString("tone")
            )
            //用于在计时器中作为唯一的标识
            simpleTimerVo.name = contentValues.getAsString("name")
            simpleTimerVo._id = contentValues.getAsInteger("_id")
            simpleTimerVo.id = "SimpleTimerVo_"+simpleTimerVo._id
            simpleTimerVo.time = contentValues.getAsLong("time")
            simpleTimerVo.order = contentValues.getAsInteger("_order")
            return simpleTimerVo
        }
    }

}

data class IntervalTimerVo(
        var secondsPrepare: Int = 0,
        var secondsTraining: Int = 0,
        var secondsRest: Int = 0,
        var group: Int = 1,
        var repeat: Int = 1,
        var tone: String = intervalRings[0].name
) : TimerVo(),Serializable{

    //结构化Interval，方便查询
    data class StructInternalTimerVo(
            val list: ArrayList<RepeatIntervalTimerVo> = ArrayList()
    ) : Serializable

    data class RepeatIntervalTimerVo(
            val prepare: SingleIntervalTimerVo,
            val list: ArrayList<GroupIntervalTimerVo> = ArrayList()
    ) : Serializable

    data class GroupIntervalTimerVo(
            val training: SingleIntervalTimerVo,
            val rest: SingleIntervalTimerVo
    ) : Serializable

    data class SingleIntervalTimerVo(
            val seconds: Int = 0,
            val type: Int
    ) : Serializable{


        var index: Int=0
        var range: Pair<Long,Long>?=null

        fun inRange(pastMilliseconds: Long) : Boolean{
            if(pastMilliseconds>=range!!.first*1000 && pastMilliseconds<=range!!.second*1000){
                return true
            }
            return false
        }

        companion object {
            const val TYPE_READY = 1
            const val TYPE_TRAINING = 2
            const val TYPE_REST = 3
        }
    }

    //返回对应的结构
    val struct by lazy {
        val s = StructInternalTimerVo()
        (0 until repeat).forEach {
            val repeatVo = RepeatIntervalTimerVo(SingleIntervalTimerVo(secondsPrepare, SingleIntervalTimerVo.TYPE_READY))
            (0 until group).forEach {
                val groupVo = GroupIntervalTimerVo(
                        SingleIntervalTimerVo(secondsPrepare, SingleIntervalTimerVo.TYPE_TRAINING),
                        SingleIntervalTimerVo(secondsPrepare, SingleIntervalTimerVo.TYPE_REST)
                )
                repeatVo.list.add(groupVo)
            }
            s.list.add(repeatVo)
        }
        //处理index和range
        var i=0
        var rangeStart = 0L
        s.list.forEach {
            it.prepare.index = i++
            it.prepare.range = Pair(rangeStart,rangeStart+secondsPrepare)
            rangeStart += secondsPrepare
            it.list.forEach {
                it.training.index = i++
                it.training.range = Pair(rangeStart,rangeStart+secondsTraining)
                rangeStart += secondsTraining

                it.rest.index = i++
                it.rest.range = Pair(rangeStart,rangeStart+secondsRest)
                rangeStart += secondsRest
            }
        }
        s
    }

    //返回当前index对应的已用时间和总时间
    fun findPastAndTotalAtIndex(pastMilliseconds: Long, index: Int) : Pair<Long,Long>{
        var target: SingleIntervalTimerVo?=null
        //找到对应组
        for (i in 0 until struct.list.size){
            val prepare = struct.list[i].prepare
            if(prepare.index == index){
                target = prepare
                break
            }
            for (j in 0 until struct.list[i].list.size){
                val training = struct.list[i].list[j].training
                if(training.index == index){
                    target = training
                    break
                }
                val rest = struct.list[i].list[j].rest
                if(rest.index == index){
                    target = rest
                    break
                }
            }
        }
        return Pair(pastMilliseconds-target!!.range!!.first*1000,target.range!!.second-target.range!!.first)
    }

    fun findIndexAndGroupAndRepeat(pastMilliseconds: Long) : TimerState?{
        //根据repeat获取对应的index
        for (i in 0 until struct.list.size){
            val prepare = struct.list[i].prepare
            if(prepare.inRange(pastMilliseconds)){
                return TimerState(prepare.index,0,i,SingleIntervalTimerVo.TYPE_READY,prepare.range)
            }
            for (j in 0 until struct.list[i].list.size){
                val training = struct.list[i].list[j].training
                if(training.inRange(pastMilliseconds)){
                    return TimerState(training.index,j,i,SingleIntervalTimerVo.TYPE_TRAINING,training.range)
                }
                val rest = struct.list[i].list[j].rest
                if(rest.inRange(pastMilliseconds)){
                    return TimerState(rest.index,j,i,SingleIntervalTimerVo.TYPE_REST,rest.range)
                }
            }
        }
        return null
    }

    val list by lazy {
        val list = java.util.ArrayList<Int>()
        (0 until repeat).forEach {
            list.add(secondsPrepare)
            (0 until group).forEach {
                list.add(secondsTraining)
                list.add(secondsRest)
            }
        }
        list
    }

    fun toCV() : ContentValues{
        val contentValues = ContentValues()
        contentValues.put("name",name)
        contentValues.put("seconds_prepare",secondsPrepare)
        contentValues.put("seconds_Training",secondsTraining)
        contentValues.put("seconds_rest",secondsRest)
        contentValues.put("group_",group)
        contentValues.put("repeat",repeat)
        contentValues.put("tone",tone)
        contentValues.put("time",System.currentTimeMillis())
        if(order==-1){
            order = DBManager.queryMAXOrder()+1
        }
        contentValues.put("_order",order)
        return contentValues
    }

    companion object {
        fun fromCV(contentValues: ContentValues) : IntervalTimerVo{
            val intervalTimerVo = IntervalTimerVo(
                    contentValues.getAsInteger("seconds_prepare"),
                    contentValues.getAsInteger("seconds_Training"),
                    contentValues.getAsInteger("seconds_rest"),
                    contentValues.getAsInteger("group_"),
                    contentValues.getAsInteger("repeat"),
                    contentValues.getAsString("tone")
            )
            //用于在计时器中作为唯一的标识
            intervalTimerVo.name = contentValues.getAsString("name")
            intervalTimerVo._id = contentValues.getAsInteger("_id")
            intervalTimerVo.id = "IntervalTimerVo_"+intervalTimerVo._id
            intervalTimerVo.time = contentValues.getAsLong("time")
            intervalTimerVo.order = contentValues.getAsInteger("_order")
            return intervalTimerVo
        }
    }
}

data class MultiTimerVo(
        val multiSubTimerVoList: ArrayList<MultiSubTimerVo> = arrayListOf()
) : TimerVo(),Serializable{

    //找到当前时间所在index
    fun findIndexAndPast(pastMilliseconds: Long) : Pair<Int,Long>{
        var seconds = 0
        var targetIndex = 0
        multiSubTimerVoList.forEachIndexed { index, subTimerVo ->
            seconds += subTimerVo.seconds*1000
            if(pastMilliseconds <= seconds){
                targetIndex = index
                return Pair(targetIndex,pastMilliseconds-(seconds-subTimerVo.seconds*1000))
            }
        }
        return Pair(targetIndex,pastMilliseconds)
    }

    fun toCV() : ContentValues{
        val contentValues = ContentValues()
        contentValues.put("name",name)
        //用json来存，方便
        contentValues.put("content",Gson().toJson(multiSubTimerVoList))
        contentValues.put("time",System.currentTimeMillis())
        if(order==-1){
            //更新为数据库的最后一个
            order = DBManager.queryMAXOrder()+1
        }
        contentValues.put("_order",order)
        return contentValues
    }

    companion object {
        fun fromCV(contentValues: ContentValues) : MultiTimerVo{
            val multiTimerVo = MultiTimerVo(
                    Gson().fromJson(contentValues.getAsString("content"),object : TypeToken<ArrayList<MultiSubTimerVo>>(){}.type)
            )
            //用于在计时器中作为唯一的标识
            multiTimerVo.name = contentValues.getAsString("name")
            multiTimerVo._id = contentValues.getAsInteger("_id")
            multiTimerVo.id = "MultiTimerVo_"+multiTimerVo._id
            multiTimerVo.time = contentValues.getAsLong("time")
            multiTimerVo.order = contentValues.getAsInteger("_order")
            return multiTimerVo
        }
    }

}

data class MultiSubTimerVo(
        val seconds: Int,
        val tone: String,
        val index: Int,
        val name: String
) : Serializable

data class Rings(
        val name: String,
        val type: Int,
        val resIds: Array<Int>?=null,
        val paths: Array<String>?=null
){
    companion object {
        const val TYPE_SIMPLE = 1
        const val TYPE_MULTI = 2
        const val TYPE_INTERVAL = 3
    }
}