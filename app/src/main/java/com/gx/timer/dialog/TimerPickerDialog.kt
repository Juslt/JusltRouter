package com.gx.timer.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import kotlinx.android.synthetic.main.d_timer_picker.*

class TimerPickerDialog : DialogFragment(){

    private val hours = (0..99).toList()
    private val minute = (0..59).toList()
    private val seconds = (0..59).toList()

    private var currentHour = 0
    private var currentMinute = 0
    private var currentSecond = 0

    var listener:((hour:Int, minute:Int, second:Int)->Unit)?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.d_timer_picker,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        np_hour.displayedValues = hours.map { "$it" }.toTypedArray()
        np_hour.maxValue = hours.size-1
        np_hour.minValue = 0
        np_hour.value = currentHour

        np_minute.displayedValues = minute.map { "$it" }.toTypedArray()
        np_minute.maxValue = minute.size-1
        np_minute.minValue = 0
        np_minute.value = currentMinute

        np_second.displayedValues = seconds.map { "$it" }.toTypedArray()
        np_second.maxValue = seconds.size-1
        np_second.minValue = 0
        np_second.value = currentSecond

        tv_cancel.setOnClickListener { dismissAllowingStateLoss() }
        tv_submit.setOnClickListener {
            listener?.invoke(np_hour.value,np_minute.value,np_second.value)
            dismissAllowingStateLoss()
        }
    }

    fun setCurrentTime(seconds: Int){
        currentHour = seconds/3600
        currentMinute = seconds%3600/60
        currentSecond = seconds%3600%60
    }
}