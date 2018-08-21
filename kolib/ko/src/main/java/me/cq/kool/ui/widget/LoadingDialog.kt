package me.cq.kool.ui.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.Gravity
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import me.cq.kool.R

/**
 * Created by phelps on 2018/2/6 0006.
 */
class LoadingDialog : AppCompatDialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val customView = LinearLayout(context)
        customView.orientation = LinearLayout.VERTICAL
        val padding = resources.getDimensionPixelSize(R.dimen.ko_padding_huge)
        customView.setPadding(padding,padding,padding,padding)
        customView.setBackgroundResource(R.drawable.ko_shape_corner_black_transparent_4dp)
        customView.gravity = Gravity.CENTER

        val ivLoading = ImageView(context)
        ivLoading.setImageResource(R.drawable.ko_ios_loading)
        customView.addView(ivLoading)

        customView.post {
            val animator = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f)
            animator.repeatMode = ValueAnimator.RESTART
            animator.repeatCount = ValueAnimator.INFINITE
            animator.duration = 1500
            animator.interpolator = LinearInterpolator()
            animator.start()
        }

        val dialog = Dialog(context)
        dialog.setContentView(customView)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}