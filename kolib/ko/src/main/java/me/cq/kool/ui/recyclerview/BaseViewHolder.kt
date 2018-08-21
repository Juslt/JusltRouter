package me.cq.kool.ui.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by phelps on 2018/1/29 0029.
 */
abstract class BaseViewHolder constructor(itemView: View, val mAdapter: RVBaseAdapter) : RecyclerView.ViewHolder(itemView) {

    constructor(resId: Int,parent: ViewGroup,adapter: RVBaseAdapter) : this(
            LayoutInflater.from(parent.context).inflate(resId,parent,false),
            adapter
    )


    abstract fun update(obj: Any, position: Int)
    fun reset(){}
}
