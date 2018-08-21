package me.cq.kool.ui.recyclerview

import android.text.TextUtils

/**
 * Created by phelps on 2018/1/29 0029.
 */

class RVFooterVo @JvmOverloads constructor(statusNormal: String? = null, statusNoMore: String? = null) {

    private var statusNormal: String? = null
    private var statusNoMore: String? = null

    var title: String? = null
    var status: Int = 0

    init {
        this.status = NORMAL
        this.statusNormal = statusNormal
        this.statusNoMore = statusNoMore

        if (TextUtils.isEmpty(statusNormal)) {
            this.statusNormal = "加载更多"
        }
        if (TextUtils.isEmpty(statusNoMore)) {
            this.statusNoMore = "没有更多"
        }
    }

    fun update(hasMore: Boolean) {
        if (hasMore) {
            status = NORMAL
            title = statusNormal
        } else {
            status = NO_MORE
            title = statusNoMore
        }
    }

    companion object {

        val NORMAL = 1
        val LOADING = 2
        val NO_MORE = 3
    }

}