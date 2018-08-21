package me.cq.kool.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import me.cq.kool.R

/**
 * Created by phelps on 2018/1/29 0029.
 */

class MoreFooterHolder(itemView: View, adapter: RVBaseAdapter) : BaseViewHolder(itemView, adapter) {

    private val tvLoad by lazy { itemView.findViewById<TextView>(R.id.tv_load) }
    private val pb by lazy { itemView.findViewById<ProgressBar>(R.id.pb) }

    private var mFooterVo: RVFooterVo? = null

    override fun update(obj: Any, position: Int) {
        mFooterVo = obj as RVFooterVo
        tvLoad.text = mFooterVo!!.title

        updateByStatus()

        tvLoad.setOnClickListener {
            mFooterVo!!.status = RVFooterVo.LOADING
            updateByStatus()
            mAdapter.loadMore()
        }
    }

    private fun updateByStatus() {
        tvLoad.isEnabled = false
        if (mFooterVo!!.status === RVFooterVo.LOADING) {
            tvLoad.visibility = View.GONE
            pb.visibility = View.VISIBLE
        } else if (mFooterVo!!.status === RVFooterVo.NORMAL) {
            tvLoad.visibility = View.VISIBLE
            pb.visibility = View.GONE
            tvLoad.isEnabled = true
        } else {
            tvLoad.visibility = View.VISIBLE
            pb.visibility = View.GONE
        }
    }

    companion object {

        fun create(parent: ViewGroup, adapter: RVBaseAdapter): MoreFooterHolder {
            return MoreFooterHolder(LayoutInflater.from(parent.context).inflate(R.layout.ko_h_footer_more, parent, false), adapter)
        }
    }
}
