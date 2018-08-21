package me.cq.timer.timer

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.controller.TimerBus
import com.gx.timer.controller.TimerDevice
import com.gx.timer.controller.TimerSession
import kotlinx.android.synthetic.main.f_timer_simple.*
import me.cq.kool.ext.dip2px
import me.cq.kool.ext.secondToHourStr
import me.cq.router.api.Router
import me.cq.timer.common.BaseFragment
import me.cq.timer.common.SimpleTimerVo
import me.cq.timer.common.Timers
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher

class SimpleTimerFragment : BaseFragment(){

    private lateinit var timerVo: SimpleTimerVo
    private var timerSession: TimerSession?=null

    private val timerListener = object : TimerDevice.TickCallback{

        override fun onPause(past: Long, total: Long, id: String) {

        }

        override fun onTick(past: Long, total: Long, id: String) {
            if(isResumed){
                updateUI(past,total)
            }
        }

        override fun onEnd(id: String) {
            timerSession=null

            updateUI(0,timerVo.totalSeconds)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_timer_simple,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        timerVo = (activity as TimerActivity).timerVo as SimpleTimerVo

        timerSession = TimerBus.get(timerVo.id)

        title_bar.ivClose.setImageResource(R.mipmap.ko_ic_back_black)
        title_bar.ivClose.setOnClickListener { activity?.finish() }
        title_bar.tvTitle.setTextColor(resources.getColor(R.color.text_normal))
        title_bar.tvTitle.text = timerVo.name
        title_bar.setBackgroundResource(R.color.ko_transparent)

        //字体
        tv_time.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        //重置中间计时宽度
        fl_timer.post {
            val w = (resources.displayMetrics.widthPixels - 86.dip2px).toInt()
            var layoutParams = fl_timer.layoutParams
            if(layoutParams==null){
                layoutParams = ViewGroup.LayoutParams(w,w)
            }else{
                layoutParams.width = w
                layoutParams.height = w
            }
            fl_timer.layoutParams = layoutParams
        }

        //设置监听
        iv_reset.setOnClickListener {
            if(timerSession!=null){
                timerSession?.stop()
                TimerBus.remove(timerVo.id)
                timerSession = TimerBus.add(timerVo)
                //ui重置
                updateUI(0,timerVo.totalSeconds)
            }
            timerSession=null
        }
        iv_play.setOnClickListener {
            if(timerSession==null){
                timerSession = TimerBus.add(timerVo)
                iv_play.setImageResource(R.mipmap.timer_btn_play)
            }
            if(timerSession!!.isStarted()){
                timerSession?.stop()
                iv_play.setImageResource(R.mipmap.timer_btn_play)
            }else{
                if(timerSession==null){
                    timerSession = TimerBus.add(timerVo)
                }
                timerSession?.listen(timerListener)
                timerSession?.start()
            }
        }
        iv_edit.setOnClickListener {
            val intent = Router.buildIntent(context!!, "/edit")
            intent!!.putExtra("timer_vo",timerVo)
            startActivity(intent)
        }


        if(timerSession==null){
            updateUI(0,timerVo.totalSeconds)
        }else{
            updateUI(timerSession!!.getCurrentTime(),timerVo.totalSeconds*1000)
        }

        //判断是否已经开始计时
        timerSession?.listen(timerListener)

        LBDispatcher.instance().register(this, EventLBFilter.eventTimerListRefresh)
    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        if(intent?.action == EventLBFilter.eventTimerListRefresh){
            (activity as TimerActivity).timerVo = Timers.findSimple((activity as TimerActivity).timerVo.id)!!
            timerVo = (activity as TimerActivity).timerVo as SimpleTimerVo
            updateUI(0,timerVo.totalSeconds)
        }
    }

    private fun updateUI(past: Long, total: Long){
        tv_time?.post {
            when{
                timerSession==null -> {
                    //重置状态
                    tv_time.text = timerVo.seconds.secondToHourStr
                    progress_view.setProgress(0f)

                    iv_reset.isEnabled = false
                    iv_edit.isEnabled = true
                    iv_play.setImageResource(R.mipmap.timer_btn_play)
                }
                timerSession!!.isStarted() -> {
                    //进行中
                    tv_time?.text = (timerVo.seconds-(Math.round(past.toFloat()/1000))).secondToHourStr
                    progress_view.setProgress(past.toFloat()/total)

                    iv_reset.isEnabled = true
                    iv_edit.isEnabled = false
                    iv_play.setImageResource(R.mipmap.timer_btn_stop)
                }
                else -> {
                    //暂停中
                    tv_time?.text = (timerVo.seconds-(Math.round(past.toFloat()/1000))).secondToHourStr
                    progress_view.setProgress(past.toFloat()/total)

                    iv_reset.isEnabled = true
                    iv_edit.isEnabled = false
                    iv_play.setImageResource(R.mipmap.timer_btn_play)
                }
            }
        }
    }

}