package me.cq.kool.app

import android.app.Application
import me.cq.kool.Kool

/**
 * Created by cq on 2017/12/11.
 */
class App : Application(){

    override fun onCreate() {
        super.onCreate()
        Kool.init(this)
    }

}