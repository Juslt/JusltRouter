package me.cq.kool.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.net.Uri


/**
 * Created by phelps on 2018/4/10 0010.
 */
object IntentHelper{

    fun dialPhone(phoneNum: String,context: Context) {
        val intent = Intent(ACTION_DIAL)
        val data = Uri.parse("tel:" + phoneNum)
        intent.data = data
        context.startActivity(intent)
    }

}