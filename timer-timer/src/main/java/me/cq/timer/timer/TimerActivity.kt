package me.cq.timer.timer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.cq.router.annotation.Route
import me.cq.timer.common.BaseActivity
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.SimpleTimerVo
import me.cq.timer.common.TimerVo
import me.cq.timer.common.lb.EventLBFilter

@Route(path = "/timer")
class TimerActivity : BaseActivity(){

    lateinit var timerVo: TimerVo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_timer)

        timerVo = intent.getSerializableExtra("timer_vo") as TimerVo

        val fragment = when(timerVo){
            is SimpleTimerVo -> SimpleTimerFragment()
            is MultiTimerVo -> MultiTimerFragment()
            else -> IntervalTimerFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

}