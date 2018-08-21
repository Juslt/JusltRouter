package me.cq.kool.utils

import me.cq.kool.Kool

/**
 * Created by phelps on 2018/1/27 0027.
 */
object DeviceHelper{

    val versionName by lazy { Kool.getContext().packageManager.getPackageInfo(Kool.getContext().packageName,0).versionName }
    val versionCode by lazy { Kool.getContext().packageManager.getPackageInfo(Kool.getContext().packageName,0).versionCode }

}