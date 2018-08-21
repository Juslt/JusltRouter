package me.cq.kool

import android.content.Context
import android.widget.Toast

/**
 * Created by phelps on 2017/12/10.
 */

fun toast(msg: String, context: Context = Kool.getContext(), length: Int = Toast.LENGTH_SHORT){
    if(Kool.mThread == Thread.currentThread()){
        Toast.makeText(context,msg,length).show()
    }else{
        Kool.mHandler.post {
            Toast.makeText(context,msg,length).show()
        }
    }
}

fun post(delay: Long = 0, f: ()->Unit){
    if(delay<=0){
        if(Kool.mThread == Thread.currentThread()){
            f()
        }else{
            Kool.mHandler.post(f)
        }
    }else{
        Kool.mHandler.postDelayed(f,delay)
    }
}