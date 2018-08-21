package com.gx.timer.fragment.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gx.timer.R
import com.gx.timer.activity.CreateTimerActivity
import com.gx.timer.activity.EditTimerActivity
import com.gx.timer.dialog.NumberPickerDialog
import com.gx.timer.dialog.RingPickerDialog
import com.gx.timer.dialog.TimerPickerDialog
import kotlinx.android.synthetic.main.f_edit_timer_category.*
import me.cq.kool.common.Msg
import me.cq.kool.ext.secondToHourStr
import me.cq.kool.ui.recyclerview.BaseViewHolder
import me.cq.kool.ui.recyclerview.RVBaseAdapter
import me.cq.kool.ui.recyclerview.initVertical
import me.cq.kool.ui.widget.KVView
import me.cq.kool.utils.ResHelper
import me.cq.timer.common.*
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import org.jetbrains.anko.find

class EditTimerCategoryFragment : BaseFragment(){

    val timerVo by lazy { (activity as EditTimerActivity).timerVo }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_edit_timer_category,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        et_timer_name.setText(timerVo.name)

        when(timerVo){
            is IntervalTimerVo ->prepareInterval()
            is MultiTimerVo ->prepareMulti()
            else -> prepareSimple()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        when(timerVo){
            is MultiTimerVo -> LBDispatcher.instance().unregister(this)
        }
    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        when(intent?.action){
            EventLBFilter.eventCreateMultiTimer -> refreshMultiData()
        }
    }

    private fun prepareInterval(){
        val intervalTimerVo = timerVo as IntervalTimerVo

        //view
        ll_interval.visibility = View.VISIBLE

        initKV(kv_interval_prepare,getString(R.string.ready),intervalTimerVo.secondsPrepare.secondToHourStr)
        initKV(kv_interval_training,getString(R.string.training),intervalTimerVo.secondsTraining.secondToHourStr)
        initKV(kv_interval_rest,getString(R.string.rest),intervalTimerVo.secondsRest.secondToHourStr)
        initKV(kv_interval_repeat,getString(R.string.repeat_time),intervalTimerVo.repeat.toString())
        initKV(kv_interval_sound,getString(R.string.tone),intervalTimerVo.tone)

        et_timer_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                intervalTimerVo.name = et_timer_name.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        kv_interval_prepare.setOnClickListener {
            val dialog = TimerPickerDialog()
            dialog.setCurrentTime(intervalTimerVo.secondsPrepare)
            dialog.listener = {hour, minute, second ->
                val totalSeconds = hour*3600 + minute*60 + second
                intervalTimerVo.secondsPrepare = totalSeconds
                kv_interval_prepare.value = totalSeconds.secondToHourStr
            }
            dialog.show(childFragmentManager,"")
        }

        kv_interval_training.setOnClickListener {
            val dialog = TimerPickerDialog()
            dialog.setCurrentTime(intervalTimerVo.secondsTraining)
            dialog.listener = {hour, minute, second ->
                val totalSeconds = hour*3600 + minute*60 + second
                intervalTimerVo.secondsTraining = totalSeconds
                kv_interval_training.value = totalSeconds.secondToHourStr
            }
            dialog.show(childFragmentManager,"")
        }

        kv_interval_rest.setOnClickListener {
            val dialog = TimerPickerDialog()
            dialog.setCurrentTime(intervalTimerVo.secondsRest)
            dialog.listener = {hour, minute, second ->
                val totalSeconds = hour*3600 + minute*60 + second
                intervalTimerVo.secondsRest = totalSeconds
                kv_interval_rest.value = totalSeconds.secondToHourStr
            }
            dialog.show(childFragmentManager,"")
        }

        kv_interval_repeat.setOnClickListener {
            val dialog = NumberPickerDialog()
            dialog.setCurrentNumber(intervalTimerVo.repeat-1)
            dialog.listener = {number ->
                intervalTimerVo.repeat = number
                kv_interval_repeat.value = "$number"
            }
            dialog.show(childFragmentManager,"")
        }

        kv_interval_sound.setOnClickListener {
            val dialog = RingPickerDialog()
            dialog.listener = {
                val ring = intervalRings[it]
                intervalTimerVo.tone = ring.name
                kv_simple_sound.value = ring.name
            }
            dialog.setData(intervalRings.map { it.name },0)
            dialog.show(childFragmentManager,"")
        }

    }

    private fun prepareMulti(){
        LBDispatcher.instance().register(this,EventLBFilter.eventCreateMultiTimer)

        val multiTimerVo = timerVo as MultiTimerVo

        ll_multi.visibility = View.VISIBLE

        rv_opt.initVertical()
        rv_opt.adapter = object : RVBaseAdapter(
                {
                    when(it.obj){
                        is MultiSubTimerVo -> {
                            val timerVo = it.obj
                            //删除当前副计时器
                            AlertDialog.Builder(context!!)
                                    .setTitle(getString(R.string.delete))
                                    .setMessage(getString(R.string.delete_the_sub_timer))
                                    .setPositiveButton(R.string.submit) { dialog, _ ->
                                        multiTimerVo.multiSubTimerVoList.remove(timerVo)
                                        dialog.dismiss()
                                        refreshMultiData()
                                    }
                                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss()}
                                    .show()

                        }
                        else -> (activity as EditTimerActivity).toSubTimerPage()
                    }
                }
        ){
            override fun onCreateViewHolderAfterFooter(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return CreateTimerMultiOptHolder(parent,this)
            }
        }

        rv_content.initVertical()
        rv_content.adapter = object : RVBaseAdapter(
                {
                    when(it.obj){
                        is MultiSubTimerVo -> {}
                        else -> (activity as EditTimerActivity).toSubTimerPage()
                    }
                }
        ){
            override fun onCreateViewHolderAfterFooter(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return CreateTimerMultiContentHolder(parent,this)
            }
        }
        //绑定滑动
        rv_opt.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (recyclerView?.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    rv_content.scrollBy(dx, dy)
                }
            }
        })
        rv_content.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (recyclerView?.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    rv_opt.scrollBy(dx, dy)
                }
            }
        })

        et_timer_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                multiTimerVo.name = et_timer_name.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //处理数据
        refreshMultiData()
    }

    private fun refreshMultiData(){
        val multiTimerVo = (activity as EditTimerActivity).timerVo as MultiTimerVo
        val list = arrayListOf<Any>()
        list.addAll(multiTimerVo.multiSubTimerVoList)
        list.add("add")
        (rv_opt.adapter as RVBaseAdapter).update(list)
        (rv_content.adapter as RVBaseAdapter).update(list)
    }

    private fun prepareSimple(){
        val simpleTimerVo = timerVo as SimpleTimerVo
        //view
        ll_simple.visibility = View.VISIBLE

        initKV(kv_simple_time,getString(R.string.time),simpleTimerVo.seconds.secondToHourStr)
        initKV(kv_simple_sound,getString(R.string.tone),simpleTimerVo.tone)

        kv_simple_time.setOnClickListener {
            val dialog = TimerPickerDialog()
            dialog.setCurrentTime(simpleTimerVo.seconds)
            dialog.listener = {hour, minute, second ->
                val totalSeconds = hour*3600 + minute*60 + second
                simpleTimerVo.seconds = totalSeconds
                kv_simple_time.value = totalSeconds.secondToHourStr
            }
            dialog.show(childFragmentManager,"")
        }

        et_timer_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                simpleTimerVo.name = et_timer_name.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        kv_simple_sound.setOnClickListener {
            val dialog = RingPickerDialog()
            dialog.listener = {
                val ring = simpleRings[it]
                simpleTimerVo.tone = ring.name
                kv_simple_sound.value = ring.name
            }
            dialog.setData(simpleRings.map { it.name },0)
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

    class CreateTimerMultiOptHolder(parent: ViewGroup, adapter: RVBaseAdapter) : BaseViewHolder(R.layout.h_create_timer_multi_opt, parent, adapter) {
        private val ivOpt by lazy { itemView.find<ImageView>(R.id.iv_opt) }
        override fun update(obj: Any, position: Int) {
            //multiVo 就是删除
            //add 就是增加
            when(obj){
                is MultiSubTimerVo -> ivOpt.setImageResource(R.mipmap.btn_delete_sub_timer)
                else -> ivOpt.setImageResource(R.mipmap.btn_add_sub_timer)
            }
            itemView.setOnClickListener {
                mAdapter.event(Msg(obj = obj))
            }
        }
    }

    class CreateTimerMultiContentHolder(parent: ViewGroup, adapter: RVBaseAdapter) : BaseViewHolder(R.layout.h_create_timer_multi_content, parent, adapter) {

        private val tvTitle by lazy { itemView.find<TextView>(R.id.tv_title) }
        private val tvTip by lazy { itemView.find<TextView>(R.id.tv_tip) }

        override fun update(obj: Any, position: Int) {
            //multiVo 就是删除
            //add 就是增加
            when(obj){
                is MultiSubTimerVo -> {
                    tvTitle.text = obj.name
                    tvTip.text = obj.seconds.secondToHourStr
                }
                else -> {
                    tvTitle.text = itemView.resources.getString(R.string.add_sub_timer)
                    tvTip.text = ""
                }
            }
            itemView.setOnClickListener {
                mAdapter.event(Msg(obj = obj))
            }
        }
    }
}