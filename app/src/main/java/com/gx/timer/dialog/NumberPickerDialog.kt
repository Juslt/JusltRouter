package com.gx.timer.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import kotlinx.android.synthetic.main.d_number_picker.*

class NumberPickerDialog : DialogFragment(){

    private val numbers = (1..99).toList()

    private var currentNumber= 1

    var listener:((number: Int)->Unit)?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.d_number_picker,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        np.displayedValues = numbers.map { "$it" }.toTypedArray()
        np.maxValue = numbers.size-1
        np.minValue = 0
        np.value = currentNumber

        tv_cancel.setOnClickListener { dismissAllowingStateLoss() }
        tv_submit.setOnClickListener {
            listener?.invoke(np.value+1)
            dismissAllowingStateLoss()
        }
    }

    fun setCurrentNumber(number: Int){
        currentNumber = number
    }
}