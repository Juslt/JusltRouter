package com.gx.timer.controller

abstract class TimerSession{

    abstract fun start()
    abstract fun stop()
    abstract fun pre()
    abstract fun next()
    abstract fun isStarted() : Boolean
    abstract fun getCurrentTime() : Long
    abstract fun listen(callback: TimerDevice.TickCallback)
}