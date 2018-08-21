package me.cq.timer.common.lb

import android.content.IntentFilter

class EventLBFilter : LBFilter() {

    companion object {
        const val eventCreateMultiTimer = "eventCreateMultiTimer"
        const val eventTimerListRefresh = "eventTimerListRefresh"
    }

    override fun buildIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(eventCreateMultiTimer)
        filter.addAction(eventTimerListRefresh)
        return filter
    }

}