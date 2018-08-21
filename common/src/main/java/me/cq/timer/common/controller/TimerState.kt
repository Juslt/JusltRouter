package me.cq.timer.common.controller

data class TimerState(
        val index: Int =0,
        val group: Int =0,
        val repeat: Int =0,
        val type: Int =0,
        val limit: Pair<Long, Long>?=null)