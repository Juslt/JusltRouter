package com.gx.timer.fragment.edit

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import com.gx.timer.activity.EditTimerActivity
import com.gx.timer.dialog.RingPickerDialog
import com.gx.timer.dialog.TimerPickerDialog
import kotlinx.android.synthetic.main.f_create_timer_sub.*
import me.cq.kool.ext.secondToHourStr
import me.cq.kool.ui.widget.KVView
import me.cq.kool.utils.ResHelper
import me.cq.timer.common.BaseFragment
import me.cq.timer.common.MultiSubTimerVo
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import me.cq.timer.common.multiRings
import org.jetbrains.anko.support.v4.toast

class EditTimerSubFragment : BaseFragment(){

    private var currentSeconds = 0
    private var subTimerName = ""
    private var subTimerTone = multiRings[0].name

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_create_timer_sub,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title_bar.setBackgroundResource(R.color.colorPrimary)
        title_bar.ivClose.setImageResource(R.mipmap.ic_close_white)
        title_bar.ivClose.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        title_bar.tvTitle.text = getString(R.string.sub_timer)
        title_bar.tvTitle.setTextColor(resources.getColor(R.color.ko_white))
        title_bar.ivRight.setImageResource(R.mipmap.arrow_right_white)
        title_bar.ivRight.setOnClickListener {
            if(currentSeconds<=0){
                toast(getString(R.string.timer_must_larger_than_0))
                return@setOnClickListener
            }
            subTimerName = et_sub_timer_name.text.toString()
            if(subTimerName.isNullOrEmpty()){
                toast(getString(R.string.fill_timer_name_please))
                return@setOnClickListener
            }
            val multiTimerVo = (activity as EditTimerActivity).timerVo as MultiTimerVo
            val subTimerVo = MultiSubTimerVo(currentSeconds, subTimerTone, multiTimerVo.multiSubTimerVoList.size + 1,subTimerName)
            multiTimerVo.multiSubTimerVoList.add(subTimerVo)
            activity?.supportFragmentManager?.popBackStack()
            LBDispatcher.instance().send(EventLBFilter.eventCreateMultiTimer)
        }
        title_bar.ivRight.visibility = View.VISIBLE


        initKV(kv_sub_time,getString(R.string.time),"00:00:00")
        initKV(kv_sub_sound,getString(R.string.tone),subTimerTone)

        kv_sub_time.setOnClickListener {
            val dialog = TimerPickerDialog()
            dialog.setCurrentTime(currentSeconds)
            dialog.listener = {hour, minute, second ->
                currentSeconds = hour*3600 + minute*60 + second
                kv_sub_time.value = currentSeconds.secondToHourStr
            }
            dialog.show(childFragmentManager,"")
        }

        kv_sub_sound.setOnClickListener {
            val dialog = RingPickerDialog()
            dialog.listener = {
                val ring = multiRings[it]
                subTimerTone = ring.name
                kv_sub_sound.value = ring.name
            }
            dialog.setData(multiRings.map { it.name },0)
            dialog.show(childFragmentManager,"")
        }
    }


    private fun initKV(kv: KVView, keyStr: String, hint: String){
        kv.key = keyStr
        kv.tvKey.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen.text_normal).toFloat())
        kv.hint = hint
        kv.etValue.setHintTextColor(resources.getColor(R.color.ko_black_transparent_30per))
        kv.etValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen.text_normal).toFloat())
        kv.etValue.setTextColor(ResHelper.getColor(R.color.ko_black_transparent_30per))
        kv.setBackgroundResource(R.color.ko_transparent)
    }
}