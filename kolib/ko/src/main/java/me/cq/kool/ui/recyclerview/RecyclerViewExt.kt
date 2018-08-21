package me.cq.kool.ui.recyclerview

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by phelps on 2018/1/29 0029.
 */
fun RecyclerView.initVertical(){
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    itemAnimator = DefaultItemAnimator()
}

fun RecyclerView.initGrid(spanCount: Int){
    layoutManager = GridLayoutManager(context,spanCount)
    itemAnimator = DefaultItemAnimator()
}