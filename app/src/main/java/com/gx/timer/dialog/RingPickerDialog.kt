package com.gx.timer.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import kotlinx.android.synthetic.main.d_ring_picker.*

class RingPickerDialog : DialogFragment(){

    private var dataList: List<String>?=null
    private var currentNumber= 0

    var listener:((number: Int)->Unit)?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.d_ring_picker,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        np.displayedValues = dataList!!.toTypedArray()
        np.maxValue = dataList!!.size-1
        np.minValue = 0
        np.value = currentNumber

        tv_cancel.setOnClickListener { dismissAllowingStateLoss() }
        tv_submit.setOnClickListener {
            listener?.invoke(np.value)
            dismissAllowingStateLoss()
        }
    }

    fun setData(list: List<String>,number: Int){
        dataList = list
        currentNumber = number
    }
}