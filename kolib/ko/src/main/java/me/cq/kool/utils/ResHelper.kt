package me.cq.kool.utils

import android.support.annotation.ColorRes
import me.cq.kool.Kool

object ResHelper{

    fun getColor(@ColorRes colorRes: Int) : Int{
        return Kool.getContext().resources.getColor(colorRes)
    }

}