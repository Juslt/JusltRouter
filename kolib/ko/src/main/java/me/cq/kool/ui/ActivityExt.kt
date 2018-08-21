package me.cq.kool.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.view.WindowManager
import me.cq.kool.ui.widget.LoadingDialog
import kotlin.reflect.KClass

/**
 * Created by phelps on 2018/1/27 0027.
 */
class LaunchOptions{

    var fromActivity: Context?=null
    var fromFragment: Fragment?=null
    var toActivity: KClass<out Activity>?=null
    var requestCode: Int = -9999
    val intent = Intent()

    internal fun launch(){
        if(fromActivity==null && (fromFragment==null||fromFragment?.activity==null)){
            return
        }
        if(toActivity==null){
            return
        }
        intent.setClass(fromActivity?:fromFragment!!.activity, toActivity!!.java)
        if(requestCode!=-9999){
            if(fromActivity!=null && fromActivity is Activity){
                (fromActivity as Activity).startActivityForResult(intent,requestCode)
            }else{
                fromFragment?.startActivityForResult(intent, requestCode)
            }
        }else{
            if(fromActivity!=null){
                if(fromActivity !is Activity){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                fromActivity?.startActivity(intent)
            }else{
                fromFragment?.startActivity(intent)
            }
        }
    }
}

fun launch(launchOpt: LaunchOptions.()->Unit){
    val option = LaunchOptions()
    option.launchOpt()
    option.launch()
}

fun Activity.immersive(alpha: Float=0.2f){
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mWindow = window
            var uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE   //防止系统栏隐藏时内容区域大小发生变化
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)  //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态栏遮住。
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)  //取消设置透明状态栏和导航栏
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)  //需要设置这个才能设置状态栏颜色
            mWindow.statusBarColor = ColorUtils.blendARGB(Color.TRANSPARENT,
                    Color.BLACK, alpha)  //设置状态栏颜色

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(alpha<=0.01f){
                    uiFlags = uiFlags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
            mWindow.decorView.systemUiVisibility = uiFlags
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.lightStatusText(){
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mWindow = window
            var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            mWindow.decorView.systemUiVisibility = uiFlags
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.darkStatusText(){
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mWindow = window
            var uiFlags = mWindow.decorView.systemUiVisibility
            uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            mWindow.decorView.systemUiVisibility = uiFlags
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}