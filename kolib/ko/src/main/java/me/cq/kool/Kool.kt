package me.cq.kool

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference

/**
 * Created by phelps on 2017/12/10.
 */
object Kool{

    val mHandler by lazy { Handler(Looper.getMainLooper()) }
    val mThread by lazy { Looper.getMainLooper().thread!! }

    private lateinit var ctxRef: WeakReference<Context>

    //初始化一些全局变量
    fun init(ctx: Context){
        ctxRef = WeakReference(ctx)
    }

    fun getContext() : Context{
        return ctxRef.get()!!
    }

}