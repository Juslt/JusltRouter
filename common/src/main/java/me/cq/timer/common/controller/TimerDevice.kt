package com.gx.timer.controller

import me.cq.kool.post
import me.cq.timer.common.IntervalTimerVo
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.TimerVo
import java.lang.ref.WeakReference

//根据一个定时器对象 来构造一个计时器
class TimerDevice(val timerVo: TimerVo) : TimerSession(){
    override fun getCurrentTime(): Long {
        return pastMilliseconds
    }

    //当前计时器的总毫秒数
    private val totalMilliseconds= timerVo.totalSeconds*1000
    private var pastMilliseconds = 0L

    private var started = false
    private var ended = false

    //注册的回调
    private val callbackList = ArrayList<WeakReference<TickCallback>>()

    /**
     * 每过一定间隔会被TimerBus调用，这时候检查当前计时器状态
     */
    fun tick(){
        synchronized(this){
            if(!started || ended){
                return
            }

            //加上间隔的时间
            pastMilliseconds += TimerBus.ProcessHandler.interval

            //如果已经到达时间，则需要移除当前device,但是最后一次回调仍然要执行
            if(pastMilliseconds >= totalMilliseconds){
                pastMilliseconds = totalMilliseconds
                ended = true
            }

            //处理注册的回调
//            callbackList.forEach {
//                post {
//                    if(ended){
//                        it.get()?.onEnd()
//                    }else{
//                        it.get()?.onTick(pastMilliseconds,totalMilliseconds)
//                    }
//                }
//            }
            //处理删除空的引用
            val iterator = callbackList.iterator()
            while(iterator.hasNext()){
                val next = iterator.next()
                val it = next.get()
                if(it==null){
                    iterator.remove()
                }else{
                    post {
                        if(ended){
                            TimerBus.remove(timerVo.id)
                            it.onEnd(timerVo.id)
                        }else{
                            it.onTick(pastMilliseconds, totalMilliseconds, timerVo.id)
                        }
                    }
                }
            }
        }
    }

    override fun start() {
        synchronized(this){
            started = true
        }
    }

    override fun stop() {
        synchronized(this){
            started = false
            post {
                //处理删除空的引用
                val iterator = callbackList.iterator()
                while(iterator.hasNext()){
                    val next = iterator.next()
                    val it = next.get()
                    if(it==null){
                        iterator.remove()
                    }else{
                        post {
                            it.onPause(pastMilliseconds, totalMilliseconds, timerVo.id)
                        }
                    }
                }
            }
        }
    }

    override fun pre() {
        synchronized(this){
            if(!started){
                return
            }
            when(timerVo){
                is IntervalTimerVo -> {
                    val timerState = timerVo.findIndexAndGroupAndRepeat(pastMilliseconds)
                    pastMilliseconds = if(timerState!!.index==0){
                        0
                    }else{
                        timerVo.list.subList(0,timerState.index-1).sumBy { it*1000 }.toLong()
                    }
                }
                is MultiTimerVo -> {
                    val (index,_) = timerVo.findIndexAndPast(pastMilliseconds)
                    pastMilliseconds = if(index==0){
                        0
                    }else{
                        timerVo.multiSubTimerVoList.subList(0,index-1).sumBy { it.seconds*1000 }.toLong()
                    }
                }
            }
        }
    }

    override fun next() {
        synchronized(this){
            if(!started){
                return
            }
            when(timerVo){
                is IntervalTimerVo -> {
                    val timerState = timerVo.findIndexAndGroupAndRepeat(pastMilliseconds)
                    if(timerState!!.index==timerVo.list.size-1){
                        return
                    }else{
                        pastMilliseconds = timerVo.list.subList(0,timerState.index+1).sumBy { it*1000 }.toLong()
                    }
                }
                is MultiTimerVo -> {
                    val (index,_) = timerVo.findIndexAndPast(pastMilliseconds)
                    if(index==timerVo.multiSubTimerVoList.size-1){
                        return
                    }else{
                        pastMilliseconds = timerVo.multiSubTimerVoList.subList(0,index+1).sumBy { it.seconds*1000 }.toLong()
                    }
                }
            }
        }
    }

    override fun isStarted(): Boolean {
        return started && !ended
    }

    override fun listen(callback: TickCallback) {
        callbackList.add(WeakReference(callback))
    }

    interface TickCallback{
        fun onTick(past: Long, total: Long, id: String)
        fun onPause(past: Long, total: Long, id: String)
        fun onEnd(id: String)
    }
}