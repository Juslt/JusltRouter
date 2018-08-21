package me.cq.kool.log

import android.util.Log

/**
 * Created by phelps on 2018/1/22 0022.
 */

fun debug(message: String?, tag: String=LogVo.tagDefault){
    if(LogVo.openLog){
        printLog(LogVo(message = message?:"",tag = tag, level = Log.DEBUG))
    }
}

private fun printLog(logVo: LogVo){
    when(logVo.level){
        Log.DEBUG -> {
            Log.d(logVo.tag,logVo.message)
            if(LogVo.openLogToCache){
                LogVo.logVoList.add(logVo)
            }
        }
    }
}
