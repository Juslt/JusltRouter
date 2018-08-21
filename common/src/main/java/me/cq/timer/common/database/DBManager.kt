package me.cq.timer.common.database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import me.cq.timer.common.IntervalTimerVo
import me.cq.timer.common.MultiTimerVo
import me.cq.timer.common.SimpleTimerVo
import me.cq.timer.common.TimerVo

object DBManager{

    var mDBHelper: DBHelper?=null

    fun init(context: Context){
        mDBHelper = DBHelper(context)
    }

    fun update(timerVo: TimerVo) : Int{
        return when(timerVo){
            is SimpleTimerVo -> updateSimple(timerVo)
            is MultiTimerVo -> updateMulti(timerVo)
            is IntervalTimerVo -> updateInterval(timerVo)
            else -> 0
        }
    }

    fun delete(timerVo: TimerVo) : Int{
        return when(timerVo){
            is SimpleTimerVo -> deleteSimple(timerVo)
            is MultiTimerVo -> deleteMulti(timerVo)
            is IntervalTimerVo -> deleteInterval(timerVo)
            else -> 0
        }
    }

    fun insert(timerVo: TimerVo) : Int{
        return when(timerVo){
            is SimpleTimerVo -> insertSimple(timerVo)
            is MultiTimerVo -> insertMulti(timerVo)
            is IntervalTimerVo -> insertInterval(timerVo)
            else -> 0
        }
    }

    fun queryMAXOrder() : Int{
        val strSimple = "select max(_order) from "
        //simple
        val simpleCursor = mDBHelper?.writableDatabase?.rawQuery(strSimple+DBHelper.TABLE_NAME_SIMPLE,null)
        simpleCursor?.moveToFirst()
        val simpleOrderMax = simpleCursor?.getLong(0)?:1
        simpleCursor?.close()
        //interval
        val intervalCursor = mDBHelper?.writableDatabase?.rawQuery(strSimple+DBHelper.TABLE_NAME_SIMPLE,null)
        intervalCursor?.moveToFirst()
        val intervalOrderMax = intervalCursor?.getLong(0)?:1
        intervalCursor?.close()
        //multi
        val multiCursor = mDBHelper?.writableDatabase?.rawQuery(strSimple+DBHelper.TABLE_NAME_SIMPLE,null)
        multiCursor?.moveToFirst()
        val multiOrderMax = multiCursor?.getLong(0)?:1
        multiCursor?.close()

        return maxOf(simpleOrderMax,intervalOrderMax,multiOrderMax).toInt()
    }

    fun queryAllCount() : Int{
        return querySimpleCount() + queryMultiCount() + queryIntervalCount()
    }

    private fun insertInterval(intervalTimerVo: IntervalTimerVo) : Int{
        return mDBHelper?.writableDatabase?.insert(DBHelper.TABLE_NAME_INTERVAL,null,intervalTimerVo.toCV())!!.toInt()
    }

    private fun updateInterval(intervalTimerVo: IntervalTimerVo) : Int{
        return mDBHelper?.writableDatabase?.update(
                DBHelper.TABLE_NAME_INTERVAL,
                intervalTimerVo.toCV(),
                "_id=?",
                arrayOf("${intervalTimerVo._id}"))!!
    }

    private fun deleteInterval(intervalTimerVo: IntervalTimerVo) : Int{
        return mDBHelper?.writableDatabase?.delete(
                DBHelper.TABLE_NAME_INTERVAL,
                "_id=?",
                arrayOf("${intervalTimerVo._id}"))!!
    }

    private fun queryIntervalCount() : Int{
        val cursor = mDBHelper?.readableDatabase?.rawQuery("select count(*) from ${DBHelper.TABLE_NAME_INTERVAL}",null)
        cursor?.moveToFirst()
        val count = cursor?.getLong(0)?:0
        cursor?.close()
        return count.toInt()
    }

    fun queryIntervalList() : ArrayList<IntervalTimerVo>{
        val cursor = mDBHelper?.readableDatabase?.query(DBHelper.TABLE_NAME_INTERVAL,null, null, null, null, null, "_order asc")
        val list = arrayListOf<IntervalTimerVo>()
        cursor?.let {
            while (cursor.moveToNext()){
                val cv = ContentValues()
                DatabaseUtils.cursorIntToContentValues(cursor,"_id",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"name",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"seconds_prepare",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"seconds_Training",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"seconds_rest",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"group_",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"repeat",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"tone",cv)
                DatabaseUtils.cursorLongToContentValues(cursor,"time",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"_order",cv)
                list.add(IntervalTimerVo.fromCV(cv))
            }
        }
        return list
    }


    private fun insertMulti(multiTimerVo: MultiTimerVo) : Int{
        return mDBHelper?.writableDatabase?.insert(DBHelper.TABLE_NAME_MULTI,null,multiTimerVo.toCV())!!.toInt()
    }

    private fun updateMulti(multiTimerVo: MultiTimerVo) : Int{
        return mDBHelper?.writableDatabase?.update(
                DBHelper.TABLE_NAME_MULTI,
                multiTimerVo.toCV(),
                "_id=?",
                arrayOf("${multiTimerVo._id}"))!!
    }

    private fun deleteMulti(multiTimerVo: MultiTimerVo) : Int{
        return mDBHelper?.writableDatabase?.delete(
                DBHelper.TABLE_NAME_MULTI,
                "_id=?",
                arrayOf("${multiTimerVo._id}"))!!
    }

    private fun queryMultiCount() : Int{
        val cursor = mDBHelper?.readableDatabase?.rawQuery("select count(*) from ${DBHelper.TABLE_NAME_MULTI}",null)
        cursor?.moveToFirst()
        val count = cursor?.getLong(0)?:0
        cursor?.close()
        return count.toInt()
    }

    fun queryMultiList() : ArrayList<MultiTimerVo>{
        val cursor = mDBHelper?.readableDatabase?.query(DBHelper.TABLE_NAME_MULTI, null, null, null, null, null, "_order asc")
        val list = arrayListOf<MultiTimerVo>()
        cursor?.let {
            while (cursor.moveToNext()){
                val cv = ContentValues()
                DatabaseUtils.cursorIntToContentValues(cursor,"_id",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"name",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"content",cv)
                DatabaseUtils.cursorLongToContentValues(cursor,"time",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"_order",cv)
                list.add(MultiTimerVo.fromCV(cv))
            }
        }
        return list
    }

    private fun insertSimple(simpleTimerVo: SimpleTimerVo) : Int{
        return mDBHelper?.writableDatabase?.insert(DBHelper.TABLE_NAME_SIMPLE,null,simpleTimerVo.toCV())!!.toInt()
    }

    private fun updateSimple(simpleTimerVo: SimpleTimerVo) : Int{
        return mDBHelper?.writableDatabase?.update(
                DBHelper.TABLE_NAME_SIMPLE,
                simpleTimerVo.toCV(),
                "_id=?",
                arrayOf("${simpleTimerVo._id}"))!!
    }

    private fun deleteSimple(simpleTimerVo: SimpleTimerVo) : Int{
        return mDBHelper?.writableDatabase?.delete(
                DBHelper.TABLE_NAME_SIMPLE,
                "_id=?",
                arrayOf("${simpleTimerVo._id}"))!!
    }

    private fun querySimpleCount() : Int{
        val cursor = mDBHelper?.readableDatabase?.rawQuery("select count(*) from ${DBHelper.TABLE_NAME_SIMPLE}",null)
        cursor?.moveToFirst()
        val count = cursor?.getLong(0)?:0
        cursor?.close()
        return count.toInt()
    }

    fun querySimpleList() : ArrayList<SimpleTimerVo>{
        val cursor = mDBHelper?.readableDatabase?.query(DBHelper.TABLE_NAME_SIMPLE, null, null, null, null, null, "_order asc")
        val list = arrayListOf<SimpleTimerVo>()
        cursor?.let {
            while (cursor.moveToNext()){
                val cv = ContentValues()
                DatabaseUtils.cursorIntToContentValues(cursor,"_id",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"name",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"seconds",cv)
                DatabaseUtils.cursorStringToContentValues(cursor,"tone",cv)
                DatabaseUtils.cursorLongToContentValues(cursor,"time",cv)
                DatabaseUtils.cursorIntToContentValues(cursor,"_order",cv)
                list.add(SimpleTimerVo.fromCV(cv))
            }
        }
        return list
    }
}