package me.cq.timer.common

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool

object Sounder{

    private var soundPool: SoundPool?=null

    private val simpleSoundMap = HashMap<TimerVo,Int>()
    private val internalSoundMap = HashMap<TimerVo,Array<Int>>()
    private val multiSoundMap = HashMap<TimerVo,Array<Int>>()

    private fun init(){
        if(soundPool==null){
            soundPool = SoundPool(255,AudioManager.STREAM_MUSIC,0)
        }
    }

    fun load(timerVo: TimerVo,context: Context){
        init()
        when(timerVo){
            is SimpleTimerVo -> {
                val ring = simpleRings.find { it.name == timerVo.tone }
                val streamId = soundPool!!.load(context, ring!!.resIds!![0], 0)
                simpleSoundMap[timerVo] = streamId
            }
            is IntervalTimerVo -> {
                val ring = intervalRings.find { it.name == timerVo.tone }
                val streamIdPrepare = soundPool!!.load(context, ring!!.resIds!![0], 0)
                val streamIdTraining = soundPool!!.load(context, ring!!.resIds!![1], 0)
                val streamIdRest = soundPool!!.load(context, ring!!.resIds!![2], 0)
                internalSoundMap[timerVo] = arrayOf(streamIdPrepare,streamIdTraining,streamIdRest)
            }
            is MultiTimerVo -> {
                val multiToneArray = Array(timerVo.multiSubTimerVoList.size,{0})
                timerVo.multiSubTimerVoList.forEachIndexed { index, multiSubTimerVo ->
                    val ring = multiRings.find { it.name == multiSubTimerVo.tone }
                    val streamId = soundPool!!.load(context, ring!!.resIds!![0], 0)
                    multiToneArray[index] = streamId
                }
                multiSoundMap[timerVo] = multiToneArray
            }
        }
    }

    fun unload(timerVo: TimerVo){
        when(timerVo){
            is SimpleTimerVo -> {
                val streamId = simpleSoundMap[timerVo]
                streamId?.let { soundPool?.unload(streamId) }
                simpleSoundMap.remove(timerVo)
            }
        }
    }

    fun play(timerVo: TimerVo){
//        val timerVo = timerWrapper.timerVo
//        when(timerVo){
//            is SimpleTimerVo -> {
//                val streamId = simpleSoundMap[timerVo]
//                streamId?.let {
//                    soundPool?.play(it,1f,1f,1,0,1f)
//                }
//            }
//            is IntervalTimerVo -> {
//                val streamIds = internalSoundMap[timerVo]
//                streamIds?.let {
//                    val streamId = when{
//                        timerWrapper.currentIndex==0 -> streamIds[0]
//                        else -> streamIds[(timerWrapper.currentIndex-1)%2+1]
//                    }
//                    soundPool?.play(streamId,1f,1f,1,0,1f)
//                }
//            }
//            is MultiTimerVo -> {
//                val streamIds = multiSoundMap[timerVo]
//                streamIds?.let {
//                    val streamId = streamIds[timerWrapper.currentIndex]
//                    soundPool?.play(streamId,1f,1f,1,0,1f)
//                }
//            }
//        }
    }

    fun destroy(){
        simpleSoundMap.clear()
        internalSoundMap.clear()
        multiSoundMap.clear()
        soundPool?.release()
    }
}