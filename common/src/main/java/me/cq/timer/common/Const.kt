package me.cq.timer.common


val simpleRings = listOf(
        Rings("ios",Rings.TYPE_SIMPLE, arrayOf(R.raw.ringios),null)
)

val intervalRings = listOf(
        Rings("ios interval",Rings.TYPE_INTERVAL, arrayOf(R.raw.ringios,R.raw.ringios,R.raw.ringios),null)
)
val multiRings = listOf(
        Rings("ios multi",Rings.TYPE_MULTI, arrayOf(R.raw.ringios),null)
)