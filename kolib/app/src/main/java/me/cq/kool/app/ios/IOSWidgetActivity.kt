package me.cq.kool.app.ios

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.cq.kool.app.R
import me.cq.kool.iosstyle.dialog.DialogSimpleView
import me.cq.kool.iosstyle.dialog.SimpleDialogFragment
import me.cq.kool.toast

/**
 * Created by cq on 2017/12/29.
 */
class IOSWidgetActivity : AppCompatActivity() {

    val d by lazy { findViewById<DialogSimpleView>(R.id.ios_dialog) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ios_widget)

        d.setContent("测试标题","测试内容","测试左边","测试右边")
        d.setLeftListener { toast("点击左边") }
        d.setRightListener { toast("点击右边") }

        d.post {
            SimpleDialogFragment().show(supportFragmentManager,"")
        }
    }

}