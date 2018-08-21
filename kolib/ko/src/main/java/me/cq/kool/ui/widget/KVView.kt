package me.cq.kool.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import me.cq.kool.R
import me.cq.kool.ui.setImageResWithFixMode

/**
 * Created by phelps on 2018/1/27 0027.
 */

class KVView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    val tvKey by lazy { findViewById<TextView>(R.id.tv_key) }
    val etValue by lazy { findViewById<EditText>(R.id.et_value) }
    val ivTip by lazy { findViewById<ImageView>(R.id.iv_tip) }

    var key: CharSequence?
        get() = tvKey.text.toString()
        set(value) {
            tvKey.text = value?:""
        }

    var keyColorRes: Int=0
        set(value) {
            field = value
            tvKey.setTextColor(resources.getColor(value))
        }

    var value: String?
        get() = etValue.text.toString()
        set(value) {
            etValue.setText(value?:"")
        }

    var valueColorRes: Int=0
        set(value) {
            field = value
            etValue.setTextColor(resources.getColor(value))
        }

    var hint: String?
        get() = etValue.hint.toString()
        set(value) {
            etValue.hint = value?:""
        }

    var tip: Int? = 0
        set(value) {
            field = value
            if(field==null){
                ivTip.setImageDrawable(null)
                ivTip.visibility = View.GONE
            }else{
                ivTip.setImageResource(tip!!)
                ivTip.visibility = View.VISIBLE
            }
        }

    var editable: Boolean = false
        set(value) {
            field = value
            etValue.isFocusable = editable
            etValue.isFocusableInTouchMode = editable
        }

    var gravityEt: Int = Gravity.RIGHT
        set(value) {
            field = value
            etValue.gravity = field
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.ko_v_kv, this)
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundResource(android.R.color.white)
        val padding = resources.getDimensionPixelOffset(R.dimen.ko_padding_large)
        setPadding(padding, padding, padding, padding)
        editable = false
    }

    fun showPadding(show: Boolean) {
        val padding = resources.getDimensionPixelOffset(R.dimen.ko_padding_large)
        if (show) {
            setPadding(padding, padding, padding, padding)
        } else {
            setPadding(0, padding, 0, padding)
        }
    }

    fun setTipSize(w: Int, h: Int){
        var params = ivTip.layoutParams
        if(params == null){
            params = ViewGroup.LayoutParams(w,h)
        }else{
            params.width = w
            params.height = h
        }
        ivTip.layoutParams = params
    }

    //hack editText onClickListener
    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        if(editable){
            etValue.setOnClickListener(null)
        }else{
            etValue.setOnClickListener(l)
        }
    }
}
