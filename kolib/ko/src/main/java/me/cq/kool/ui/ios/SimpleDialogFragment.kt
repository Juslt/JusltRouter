package me.cq.kool.ui.ios

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by cq on 2017/12/29.
 */
class SimpleDialogFragment : AppCompatDialogFragment(){

    lateinit var dialogView: DialogSimpleView

    val config = DialogSimpleView.Config()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogView = DialogSimpleView(context!!)
        return dialogView
    }

    override fun onResume() {
        super.onResume()
        dialogView.updateConfig(config)
    }

    fun setCancel(cancelable: Boolean) : SimpleDialogFragment{
        isCancelable = cancelable
        return this
    }

    fun setContent(title: String?, content: String?, left: String?, right: String?) : SimpleDialogFragment{
        config.title = title
        config.content = content
        config.left = left
        config.right = right
        return this
    }

    fun listenRight(cb: ()->Unit) : SimpleDialogFragment{
        config.cbRight = cb
        return this
    }

    fun listenLeft(cb: ()->Unit) : SimpleDialogFragment{
        config.cbLeft = cb
        return this
    }
}