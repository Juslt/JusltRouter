package me.cq.kool.common

/**
 * Created by phelps on 2018/1/29 0029.
 */
data class Msg (
        val what: Int = 0,
        val arg1: Int = 0,
        val arg2: Int = 0,
        val obj: Any?=null
)
