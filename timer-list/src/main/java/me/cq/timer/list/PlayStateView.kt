package me.cq.timer.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.v_play_state.view.*

class PlayStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.v_play_state,this)
    }

    fun play(progress: Float, pause: Boolean){
        if(progress<=0){
            circle_pb.setProgress(0f)
            iv_center.setImageResource(R.mipmap.list_ic_play_center)
        }else{
            circle_pb.setProgress(progress)
            if(pause){
                iv_center.setImageResource(R.mipmap.list_ic_play_center)
            }else{
                iv_center.setImageResource(R.mipmap.list_ic_stop_center)
            }
        }
    }

}