package me.cq.timer.common.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class ExtViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    //是否可以进行滑动
    private var scrollable = true

    fun enableScrollable(scrollable: Boolean) {
        this.scrollable = scrollable
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if(scrollable){
            super.onInterceptTouchEvent(ev)
        }else{
            scrollable
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if(scrollable){
            super.onTouchEvent(ev)
        }else{
            scrollable
        }
    }
}