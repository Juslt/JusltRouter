package com.gx.timer.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import com.gx.timer.activity.EditTimerActivity
import kotlinx.android.synthetic.main.f_edit_timer.*
import me.cq.timer.common.*
import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import org.jetbrains.anko.support.v4.toast

class EditTimerFragment : BaseFragment(){

    val timerVo by lazy { (activity as EditTimerActivity).timerVo }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_edit_timer,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title_bar.ivClose.setImageResource(R.mipmap.ic_close_balck)
        title_bar.ivClose.setOnClickListener { activity?.finish() }
        title_bar.tvTitle.text = timerVo.name
        title_bar.ivRight.setImageResource(R.mipmap.ic_arrow_right_black)
        title_bar.ivRight.setOnClickListener { finishCreateTimer() }
        title_bar.ivRight.visibility = View.VISIBLE


        childFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container,EditTimerCategoryFragment())
                .commit()
    }

    private fun finishCreateTimer(){
        val parentActivity = activity as EditTimerActivity
        when(parentActivity.timerVo){
            is SimpleTimerVo -> createSimpleTimer(parentActivity)
            is MultiTimerVo -> createMultiTimer(parentActivity)
            is IntervalTimerVo -> createIntervalTimer(parentActivity)
        }
    }

    private fun createIntervalTimer(parentActivity: EditTimerActivity){

        val timerVo = parentActivity.timerVo as IntervalTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.secondsTraining<=0){
            toast(getString(R.string.training_time_can_be_0))
            return
        }
        //更新到数据库
        val result = DBManager.update(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast(getString(R.string.modify_success))
        parentActivity.finish()
        Timers.updateAll()
    }


    private fun createMultiTimer(parentActivity: EditTimerActivity){

        val timerVo = parentActivity.timerVo as MultiTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.multiSubTimerVoList.isEmpty()){
            toast(getString(R.string.sub_timer_quantity_can_not_be_0))
            return
        }
        //插入到数据库
        val result = DBManager.update(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast(getString(R.string.modify_success))
        parentActivity.finish()
        Timers.updateAll()
    }

    private fun createSimpleTimer(parentActivity: EditTimerActivity){

        val timerVo = parentActivity.timerVo as SimpleTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.seconds<=0){
            toast(getString(R.string.timer_seconds_should_not_be_0))
            return
        }
        //插入到数据库
        val result = DBManager.update(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast(getString(R.string.modify_success))
        parentActivity.finish()
        Timers.updateAll()
    }

}