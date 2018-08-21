package me.cq.kool.ui.widget

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Created by phelps on 2018/3/29 0029.
 */
class MultiTouchLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet?=null, defStyleAttr: Int=0) : FrameLayout(context, attrs, defStyleAttr) {

    private var startPoint0 = PointF()
    private val startPoint1 = PointF()
    private val startPoint2 = PointF()
    private val endPoint0 = PointF()
    private val endPoint1 = PointF()
    private val endPoint2 = PointF()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        when(event.action and MotionEvent.ACTION_MASK){

            MotionEvent.ACTION_DOWN -> {
                //第一个手指位置
                startPoint0.set(event.x,event.y)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //第二第三个手指
                val id = event.getPointerId(event.actionIndex)
                if(event.actionIndex==1){
                    startPoint1.set(event.getX(id),event.getY(id))
                }else if(event.actionIndex==2){
                    startPoint2.set(event.getX(id),event.getY(id))
                }
            }
        }

        if(event.pointerCount == 3){
            //只拦截三点触摸
            return true
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action and MotionEvent.ACTION_MASK){

            MotionEvent.ACTION_POINTER_UP -> {
                //第二第三个手指
                val id = event.getPointerId(event.actionIndex)
                if(event.actionIndex==1){
                    endPoint1.set(event.getX(id),event.getY(id))
                }else if(event.actionIndex==2){
                    endPoint2.set(event.getX(id),event.getY(id))
                }
            }

            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {

                endPoint0.set(event.x,event.y)

                me.cq.kool.log.debug("三点触摸开始: $startPoint0 , $startPoint1, $startPoint2")
                me.cq.kool.log.debug("三点触摸结束: $endPoint0 , $endPoint1, $endPoint2")

                if(startPoint0.y<endPoint0.y && startPoint1.y<endPoint1.y && startPoint2.y<endPoint2.y){
                    me.cq.kool.log.debug("三点上滑")
                }
                return true
            }
        }


        return super.onTouchEvent(event)
    }

}