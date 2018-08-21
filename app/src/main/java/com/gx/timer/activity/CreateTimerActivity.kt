package com.gx.timer.activity

import android.os.Bundle
import com.gx.timer.R
import com.gx.timer.fragment.new.CreateTimerFragment
import com.gx.timer.fragment.new.CreateTimerPagerFragment
import com.gx.timer.fragment.new.CreateTimerSubFragment
import me.cq.router.annotation.Route
import me.cq.timer.common.BaseActivity
import me.cq.timer.common.IntervalTimerVo
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.SimpleTimerVo

@Route(path = "/create")
class CreateTimerActivity : BaseActivity(){

    val simpleTimerVo = SimpleTimerVo()
    val multiTimerVo = MultiTimerVo()
    val intervalTimerVo = IntervalTimerVo()

    var type = CreateTimerPagerFragment.TYPE_SIMPLE

    private val createTimerFragment by lazy { CreateTimerFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_single)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, createTimerFragment)
                .commit()
    }

    fun toSubTimerPage(){
        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container,CreateTimerSubFragment())
                .commit()
    }

}