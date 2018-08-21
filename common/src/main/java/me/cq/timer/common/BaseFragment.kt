package me.cq.timer.common

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.Fragment
import me.cq.timer.common.lb.LBCallBack
import me.cq.timer.common.lb.LBDispatcher

open class BaseFragment : Fragment(), LBCallBack {
    override fun lbCallback(context: Context?, intent: Intent?) {

    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            view?.requestApplyInsets()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LBDispatcher.instance().unregister(this)
    }

}