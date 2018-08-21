package me.cq.kool.log

import android.util.Log

/**
 * Created by phelps on 2018/1/22 0022.
 */
data class LogVo(
        val message: String="",
        val tag: String= tagDefault,
        val level: Int=Log.DEBUG
){
    companion object {
        var tagDefault = "LLLLLL"
        var openLog = true
        var openLogToCache = true
        var logVoList = ArrayList<LogVo>()
    }
}