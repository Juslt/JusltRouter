package me.cq.kool.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import me.cq.kool.R



/**
 * Created by phelps on 2018/1/27 0027.
 */
class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    val ivClose by lazy { findViewById<ImageView>(R.id.iv_title_bar_close) }
    val tvTitle by lazy { findViewById<TextView>(R.id.tv_title_bar_title) }
    val ivRight by lazy { findViewById<ImageView>(R.id.iv_title_bar_right) }
    val tvRight by lazy { findViewById<TextView>(R.id.tv_title_bar_right) }
    val ivDivider by lazy { findViewById<ImageView>(R.id.iv_title_bar_divider) }

    init {
        LayoutInflater.from(context).inflate(R.layout.ko_v_title_bar, this)
        setBackgroundResource(android.R.color.white)
        tvTitle.setOnLongClickListener {
            globalListener?.invoke()
            return@setOnLongClickListener true
        }
    }

    fun showDivider(show: Boolean) {
        ivDivider!!.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        var globalListener: (()->Unit)?=null
    }

}
