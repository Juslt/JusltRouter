package com.gx.timer.activity

import android.os.Bundle
import com.gx.timer.R
import com.gx.timer.fragment.edit.EditTimerFragment
import com.gx.timer.fragment.edit.EditTimerSubFragment
import me.cq.router.annotation.Route
import me.cq.timer.common.BaseActivity
import me.cq.timer.common.TimerVo

@Route(path = "/edit")
class EditTimerActivity : BaseActivity(){

    val timerVo by lazy { intent.getSerializableExtra("timer_vo") as TimerVo }

    private val editTimerFragment by lazy { EditTimerFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_single)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, editTimerFragment)
                .commit()
    }

    fun toSubTimerPage(){
        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container,EditTimerSubFragment())
                .commit()
    }

}