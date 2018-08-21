package me.cq.kool.ui.ios

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.cq.kool.R

/**
 * Created by cq on 2017/12/29.
 * 简单风格的dialog
 */
class DialogSimpleView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context,attrs,0)
    constructor(context: Context) : this(context,null)

    internal val tvTitle by lazy { findViewById<TextView>(R.id.tv_title) }
    internal val tvContent by lazy { findViewById<TextView>(R.id.tv_content) }
    internal val tvLeft by lazy { findViewById<TextView>(R.id.tv_left) }
    internal val tvRight by lazy { findViewById<TextView>(R.id.tv_right) }

    internal val ivDivider1 by lazy { findViewById<ImageView>(R.id.iv_divider_1) }
    internal val ivDivider2 by lazy { findViewById<ImageView>(R.id.iv_divider_2) }
    internal val ivDivider3 by lazy { findViewById<ImageView>(R.id.iv_divider_3) }

    lateinit var config: Config

    init {
        LayoutInflater.from(context).inflate(R.layout.ko_ios_d_simple,this)
    }

    fun updateConfig(config: Config){
        config.apply {
            //有文字才显示，没文字的隐藏
            if(title.isNullOrEmpty()){
                tvTitle.visibility = View.GONE
                ivDivider1.visibility = View.GONE
            }else{
                tvTitle.text = title
            }

            if(content.isNullOrEmpty()){
                tvContent.visibility = View.GONE
                ivDivider1.visibility = View.GONE
            }else{
                tvContent.text = content
            }

            if(content.isNullOrEmpty()){
                tvLeft.visibility = View.GONE
                ivDivider3.visibility = View.GONE
            }else{
                tvLeft.text = left
            }

            if(content.isNullOrEmpty()){
                tvRight.visibility = View.GONE
                ivDivider3.visibility = View.GONE
            }else{
                tvRight.text = right
            }
            tvRight.setOnClickListener { cbRight?.invoke() }
            tvLeft.setOnClickListener { cbLeft?.invoke() }
        }

    }

    class Config{
        var title: String?=null
        var content: String?=null
        var left: String?=null
        var right: String?=null
        var cbLeft: (()->Unit)?=null
        var cbRight: (()->Unit)?=null
    }
}