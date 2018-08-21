package me.cq.timer.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.cq.kool.ui.immersive
import me.cq.timer.common.lb.LBCallBack
import me.cq.timer.common.lb.LBDispatcher

open class BaseActivity : AppCompatActivity(), LBCallBack{
    override fun lbCallback(context: Context?, intent: Intent?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immersive(0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        LBDispatcher.instance().unregister(this)
    }
}