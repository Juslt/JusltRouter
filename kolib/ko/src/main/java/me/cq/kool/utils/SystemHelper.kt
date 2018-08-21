package me.cq.kool.utils

import android.content.Context
import android.os.PowerManager
import me.cq.kool.Kool


object SystemHelper{

    private var wakeLock: PowerManager.WakeLock?= null

    fun acquireWakeLock() {
        if (wakeLock == null) {
            val pm = Kool.getContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.javaClass.canonicalName)
        }
        if(wakeLock!!.isHeld){
            return
        }
        wakeLock?.acquire()
    }


    fun releaseWakeLock() {
        wakeLock?.let {
            if(wakeLock!!.isHeld){
                wakeLock?.release()
            }
        }
    }

}