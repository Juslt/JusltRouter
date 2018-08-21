package me.cq.timer.timer

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gx.timer.controller.TimerBus
import com.gx.timer.controller.TimerDevice
import com.gx.timer.controller.TimerSession
import kotlinx.android.synthetic.main.f_timer_multi.*
import me.cq.kool.ext.dip2px
import me.cq.kool.ext.secondToHourStr
import me.cq.kool.ui.recyclerview.BaseViewHolder
import me.cq.kool.ui.recyclerview.RVBaseAdapter
import me.cq.kool.ui.recyclerview.initVertical
import me.cq.kool.utils.ResHelper
import me.cq.router.api.Router
import me.cq.timer.common.BaseFragment
import me.cq.timer.common.MultiSubTimerVo
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.Timers
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import org.jetbrains.anko.find

class MultiTimerFragment : BaseFragment(){

    private lateinit var timerVo: MultiTimerVo
    private var timerSession: TimerSession?=null
    private var currentIndex = 0

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

            updateUI(0,timerVo.totalSeconds)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_timer_multi,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timerVo = (activity as TimerActivity).timerVo as MultiTimerVo
        timerSession = TimerBus.get(timerVo.id)

        iv_back.setOnClickListener { activity?.finish() }
        tv_timer_name.text = timerVo.name

        //字体
        tv_time.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")
        tv_time_title.typeface = Typeface.createFromAsset(context!!.assets,"fonts/din.ttf")

        rv.post {
            val p = rv.measuredHeight/2 - 30.dip2px
            rv.setPadding(0, p.toInt(),0, p.toInt())
        }

        //设置监听
        iv_reset.setOnClickListener {
            if(timerSession!=null){
                timerSession?.stop()
                TimerBus.remove(timerVo.id)
                timerSession = null
                //ui重置
                updateUI(0,timerVo.totalSeconds)
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
                updateUI(0,timerVo.totalSeconds)
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

        //初始化列表
        rv.initVertical()
        rv.adapter = object : RVBaseAdapter(){
            override fun onCreateViewHolderAfterFooter(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return MultiTimerHolder(parent,this)
            }
        }
        (rv.adapter as RVBaseAdapter).update(timerVo.multiSubTimerVoList)
        (rv.adapter as RVBaseAdapter).map["index"] = currentIndex
        rv.post {
            rv.scrollToPosition(0)
        }
        if(timerSession==null){
            updateUI(0,timerVo.totalSeconds)
        }else{
            updateUI(timerSession!!.getCurrentTime(),timerVo.totalSeconds*1000)
        }


        timerSession?.listen(timerListener)

        LBDispatcher.instance().register(this, EventLBFilter.eventTimerListRefresh)
    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        if(intent?.action == EventLBFilter.eventTimerListRefresh){
            (activity as TimerActivity).timerVo = Timers.findSimple((activity as TimerActivity).timerVo.id)!!
            timerVo = (activity as TimerActivity).timerVo as MultiTimerVo
            updateUI(0,timerVo.totalSeconds)
        }
    }

    private fun updateUI(pastMilliseconds: Long, totalMilliseconds: Long){

        tv_time?.post {
            tv_time_title.text = "${(totalMilliseconds/1000-pastMilliseconds/1000).secondToHourStr}"
            when{
                timerSession==null -> {
                    //停止状态
                    updateUIByState(1)
                    val subTimerVo = timerVo.multiSubTimerVoList[0]
                    tv_time?.text = subTimerVo.seconds.secondToHourStr
                    progress_view.setProgress(0f)
                    currentIndex = 0
                    (rv.adapter as RVBaseAdapter).map["index"] = currentIndex
                    rv.adapter.notifyDataSetChanged()
                    rv.smoothScrollToPosition(currentIndex)
                }
                timerSession!!.isStarted() -> {
                    //运行状态
                    updateUIByState(2)
                    //当前所在的index
                    val (index,pastAtIndex) = timerVo.findIndexAndPast(pastMilliseconds)
                    val subTimerVo = timerVo.multiSubTimerVoList[index]
                    val progress = pastAtIndex/(subTimerVo.seconds*1000f)
                    progress_view.setProgress(progress)
                    tv_time?.text = (subTimerVo.seconds-Math.round((pastAtIndex/1000).toDouble())).secondToHourStr
                    if(index != currentIndex){
                        currentIndex = index
                        (rv.adapter as RVBaseAdapter).map["index"] = currentIndex
                        rv.adapter.notifyDataSetChanged()
                        rv.smoothScrollToPosition(currentIndex)
                    }
                }
                else -> {
                    //暂停状态
                    updateUIByState(3)

                    //当前所在的index
                    val (index,pastAtIndex) = timerVo.findIndexAndPast(pastMilliseconds)
                    val subTimerVo = timerVo.multiSubTimerVoList[index]
                    val progress = pastAtIndex/(subTimerVo.seconds*1000f)
                    progress_view.setProgress(progress)
                    tv_time?.text = (subTimerVo.seconds-Math.round((pastAtIndex/1000).toDouble())).secondToHourStr
                    if(index != currentIndex){
                        currentIndex = index
                        (rv.adapter as RVBaseAdapter).map["index"] = currentIndex
                        rv.adapter.notifyDataSetChanged()
                        rv.smoothScrollToPosition(currentIndex)
                    }
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

    class MultiTimerHolder(parent: ViewGroup, adapter: RVBaseAdapter) : BaseViewHolder(R.layout.h_timer_multi, parent, adapter){

        private val tvName by lazy { itemView.find<TextView>(R.id.tv_name) }
        private val tvTime by lazy { itemView.find<TextView>(R.id.tv_time) }

        override fun update(obj: Any, position: Int) {
            val vo = obj as MultiSubTimerVo

            tvName.text = vo.name
            tvTime.text = vo.seconds.secondToHourStr

            if(position== mAdapter.map["index"]){
                tvName.setTextColor(ResHelper.getColor(R.color.colorPrimaryDark))
                tvTime.setTextColor(ResHelper.getColor(R.color.colorPrimaryDark))
            }else{
                tvName.setTextColor(ResHelper.getColor(R.color.text_normal))
                tvTime.setTextColor(ResHelper.getColor(R.color.text_normal))
            }
        }

    }
}