package me.cq.timer.common

import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher

/**
 * 全局保存当前TimerVo实例，从数据库查询，别的地方使用只能从这里更新
 */
object Timers{

    val simpleList = ArrayList<SimpleTimerVo>()
    val multiList = ArrayList<MultiTimerVo>()
    val intervalList = ArrayList<IntervalTimerVo>()

    fun updateAll(){
        simpleList.clear()
        multiList.clear()
        intervalList.clear()

        simpleList.addAll(DBManager.querySimpleList())
        multiList.addAll(DBManager.queryMultiList())
        intervalList.addAll(DBManager.queryIntervalList())

        LBDispatcher.instance().send(EventLBFilter.eventTimerListRefresh)
    }

    fun findSimple(id: String) = simpleList.find { it.id==id }
    fun findMulti(id: String) = multiList.find { it.id==id }
    fun findInterval(id: String) = intervalList.find { it.id==id }

    fun isEmpty() : Boolean{
        return simpleList.isEmpty() && multiList.isEmpty() && intervalList.isEmpty()
    }

    fun remove(timerVo: TimerVo){
        simpleList.remove(timerVo)
        multiList.remove(timerVo)
        intervalList.remove(timerVo)
        DBManager.delete(timerVo)
    }
}