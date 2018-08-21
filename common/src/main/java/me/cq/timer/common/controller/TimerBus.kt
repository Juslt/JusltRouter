package com.gx.timer.controller

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import me.cq.kool.utils.SystemHelper
import me.cq.timer.common.TimerVo
import java.util.concurrent.CopyOnWriteArrayList

object TimerBus{

    private val deviceList = CopyOnWriteArrayList<TimerDevice>()

    private var handlerThread: HandlerThread?=null
    private var handler: Handler?=null

    init {
        if(handlerThread ==null){
            handlerThread = HandlerThread(this.javaClass.name)
            handlerThread!!.start()
            handler = ProcessHandler(deviceList,handlerThread!!.looper)
        }
    }

    fun add(timerVo: TimerVo) : TimerSession{
        synchronized(this){
            //先检查，如果已经存在，则直接返回
            var device = deviceList.find { it.timerVo.id == timerVo.id }
            if(device==null){
                //创建一个新的device加入
                device = TimerDevice(timerVo)
                deviceList.add(device)
                //开始计时
                handler?.sendEmptyMessage(ProcessHandler.TYPE_START)
            }
            return device
        }
    }

    fun remove(id: String){
        synchronized(this){
            for (device in deviceList){
                if(device.timerVo.id == id){
                    deviceList.remove(device)
                }
            }
        }
    }

    fun get(id: String) : TimerSession?{
        return deviceList.find { it.timerVo.id == id }
    }

    //负责每隔一定时间进行遍历
    class ProcessHandler(val list: CopyOnWriteArrayList<TimerDevice>, looper: Looper) : Handler(looper){

        companion object {
            //没间隔20毫秒检查一次timer
            const val interval = 50L

            const val TYPE_LOOP = 0
            const val TYPE_START = 1
            const val TYPE_STOP = 2
        }

        private var started = false

        override fun handleMessage(msg: Message) {
            when(msg.what){
                TYPE_START -> handleStart()
                TYPE_LOOP -> handleLoop()
                TYPE_STOP -> {}
            }
        }

        private fun handleLoop(){
            //遍历当前的device
            deviceList.forEach { it.tick() }
            if(deviceList.isNotEmpty()){
                sendEmptyMessageDelayed(TYPE_LOOP,interval)
            }else{
                started = false
                SystemHelper.releaseWakeLock()
            }
        }

        private fun handleStart(){
            if(started){
                return
            }
            started = true
            SystemHelper.acquireWakeLock()
            sendEmptyMessage(TYPE_LOOP)
        }
    }
}