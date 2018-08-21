package com.gx.timer

import android.app.Application
import com.facebook.stetho.Stetho
import me.cq.kool.Kool
import me.cq.router.api.Router
import me.cq.timer.common.Timers
import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher

class App : Application(){

    companion object {
        var i: App?=null
    }

    override fun onCreate() {
        super.onCreate()

        i = this

        Stetho.initializeWithDefaults(this)

        Kool.init(this)
        DBManager.init(this)

        LBDispatcher.instance().init(this, EventLBFilter())

        Router.init(this, arrayOf("app","timerlist","timertimer"))

        Timers.updateAll()
    }

}