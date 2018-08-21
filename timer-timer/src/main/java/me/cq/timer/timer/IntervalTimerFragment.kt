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
import kotlinx.android.synthetic.main.f_timer_interval.*
import me.cq.kool.ext.dip2px
import me.cq.kool.ext.secondToHourStr
import me.cq.kool.log.debug
import me.cq.router.api.Router
import me.cq.timer.common.BaseFragment
import me.cq.timer.common.IntervalTimerVo
import me.cq.timer.common.Timers
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher

class IntervalTimerFragment : BaseFragment(){

    private lateinit var timerVo: IntervalTimerVo
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
            TimerBus.remove(timerVo.id)
            timerSession=null
            updateUI(0,timerVo.totalSeconds*1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_timer_interval,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timerVo = (activity as TimerActivity).timerVo as IntervalTimerVo
        timerSession = TimerBus.get(timerVo.id)

        title_bar.ivClose.setImageResource(R.mipmap.ko_ic_back_black)
        title_bar.ivClose.setOnClickListener { activity?.finish() }
        title_bar.tvTitle.setTextColor(resources.getColor(R.color.text_normal))
        title_bar.tvTitle.text = timerVo.name
        title_bar.setBackgroundResource(R.color.ko_transparent)

        //字体
        tv_time.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        tv_repeat_times.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        tv_left_duration.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        tv_group_count.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        //重置中间计时宽度
        rl_timer.post {
            val w = (resources.displayMetrics.widthPixels - 86.dip2px).toInt()
            var layoutParams = rl_timer.layoutParams
            if(layoutParams==null){
                layoutParams = ViewGroup.LayoutParams(w,w)
            }else{
                layoutParams.width = w
                layoutParams.height = w
            }
            rl_timer.layoutParams = layoutParams
        }

        //设置监听
        iv_reset.setOnClickListener {
            if(timerSession!=null){
                timerSession?.stop()
                TimerBus.remove(timerVo.id)
                timerSession = null
                //ui重置
                updateUI(0,timerVo.totalSeconds*1000)
            }
        }
        iv_play.setOnClickListener {
            if(timerSession==null){
                timerSession = TimerBus.add(timerVo)
            }
            if(timerSession!!.isStarted()){
                timerSession?.stop()
                updateUIByState(3)
            }else{
                timerSession?.listen(timerListener)
                timerSession?.start()
                updateUIByState(2)
            }
        }
        iv_edit.setOnClickListener {
            val intent = Router.buildIntent(context!!, "/edit")
            intent!!.putExtra("timer_vo",timerVo)
            startActivity(intent)
        }
        iv_pre.setOnClickListener {
            timerSession?.pre()
        }
        iv_next.setOnClickListener {
            timerSession?.next()
        }

        if(timerSession==null){
            updateUI(0,timerVo.totalSeconds*1000)
        }else{
            updateUI(timerSession!!.getCurrentTime(),timerVo.totalSeconds*1000)
        }

        timerSession?.listen(timerListener)

        LBDispatcher.instance().register(this, EventLBFilter.eventTimerListRefresh)
    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        if(intent?.action == EventLBFilter.eventTimerListRefresh){
            (activity as TimerActivity).timerVo = Timers.findSimple((activity as TimerActivity).timerVo.id)!!
            timerVo = (activity as TimerActivity).timerVo as IntervalTimerVo
            updateUI(0,timerVo.totalSeconds)
        }
    }

    private fun updateUI(past: Long, total: Long){
        tv_time?.post {

            when{
                timerSession==null -> {
                    tv_state.text = getString(R.string.ready)
                    tv_time?.text = timerVo.list[0].secondToHourStr
                    progress_view.setProgress(0f)
                    tv_repeat_times.text = "0/${timerVo.struct.list.size}"
                    tv_left_duration.text = (total/1000).secondToHourStr
                    tv_group_count.text = "0/${timerVo.struct.list[0].list.size}"
                    updateUIByState(1)
                }
                timerSession!!.isStarted() -> {

                    val timerState = timerVo.findIndexAndGroupAndRepeat(past)
                    val (pastAtIndex,totalAtIndex) = timerVo.findPastAndTotalAtIndex(past,timerState!!.index)

                    debug("past=$past,index=${timerState.index},group=${timerState.group},repeat=${timerState.repeat},pastAtIndex=$pastAtIndex,totalAtIndex=$totalAtIndex")

                    tv_time?.text = (totalAtIndex-pastAtIndex/1000).secondToHourStr
                    tv_repeat_times.text = "${timerState.repeat}/${timerVo.struct.list.size}"
                    tv_left_duration.text = ((total-past)/1000).secondToHourStr
                    when(timerState.type){
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_READY -> {
                            tv_state.text = getString(R.string.ready)
                            tv_group_count.text = "0/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = false
                            progress_view.setProgress(pastAtIndex/(totalAtIndex*1000f))
                        }
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_TRAINING -> {
                            tv_state.text = getString(R.string.training)
                            tv_group_count.text = "${timerState.group+1}/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = true
                            progress_view.setProgress(pastAtIndex/2f/(totalAtIndex*1000f))
                        }
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_REST -> {
                            tv_state.text = getString(R.string.rest)
                            tv_group_count.text = "${timerState.group+1}/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = true
                            progress_view.setProgress(pastAtIndex/2f/(totalAtIndex*1000f)+0.5f)
                        }
                    }

                    updateUIByState(2)
                }
                else -> {

                    val timerState = timerVo.findIndexAndGroupAndRepeat(past)
                    val (pastAtIndex,totalAtIndex) = timerVo.findPastAndTotalAtIndex(past,timerState!!.index)

                    debug("past=$past,index=${timerState.index},group=${timerState.group},repeat=${timerState.repeat},pastAtIndex=$pastAtIndex,totalAtIndex=$totalAtIndex")

                    tv_time?.text = (totalAtIndex-pastAtIndex/1000).secondToHourStr
                    tv_repeat_times.text = "${timerState.repeat}/${timerVo.struct.list.size}"
                    tv_left_duration.text = ((total-past)/1000).secondToHourStr
                    when(timerState.type){
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_READY -> {
                            tv_state.text = getString(R.string.ready)
                            tv_group_count.text = "0/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = false
                            progress_view.setProgress(pastAtIndex/(totalAtIndex*1000f))
                        }
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_TRAINING -> {
                            tv_state.text = getString(R.string.training)
                            tv_group_count.text = "${timerState.group+1}/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = true
                            progress_view.setProgress(pastAtIndex/2f/(totalAtIndex*1000f))
                        }
                        IntervalTimerVo.SingleIntervalTimerVo.TYPE_REST -> {
                            tv_state.text = getString(R.string.rest)
                            tv_group_count.text = "${timerState.group+1}/${timerVo.struct.list[0].list.size}"
                            //这里特殊，休息和训练是一隔圈的，所以要 训练是progress/2f, 休息是progress/2f+0.5f
                            //间歇模式
                            progress_view.interval = true
                            progress_view.setProgress(pastAtIndex/2f/(totalAtIndex*1000f)+0.5f)
                        }
                    }
                    updateUIByState(3)
                }
            }
        }
    }

    private fun updateUIByState(state: Int){
        when(state){
            1 -> { //停止
                iv_reset.isEnabled = false
                iv_pre.isEnabled = false
                iv_next.isEnabled = false
                iv_edit.isEnabled = true
                iv_play.setImageResource(R.mipmap.timer_btn_play)
            }
            2 -> { //运行
                iv_reset.isEnabled = true
                iv_pre.isEnabled = true
                iv_next.isEnabled = true
                iv_edit.isEnabled = false
                iv_play.setImageResource(R.mipmap.timer_btn_stop)
            }
            3 -> { //暂停
                iv_reset.isEnabled = true
                iv_pre.isEnabled = false
                iv_next.isEnabled = false
                iv_edit.isEnabled = false
                iv_play.setImageResource(R.mipmap.timer_btn_play)
            }
        }
    }
}