package me.cq.timer.common.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "gx_timer_db", null, 1) {

    companion object {
        val TABLE_NAME_SIMPLE = "simple_timer"
        val TABLE_NAME_INTERVAL = "interval_timer"
        val TABLE_NAME_MULTI = "multi_timer"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //建立三中timer对应的表
        val simpleTableStr ="""
           CREATE TABLE IF NOT EXISTS simple_timer(
                _id            INTEGER PRIMARY KEY AUTOINCREMENT,
                name           TEXT,
                seconds        INTEGER,
                tone           TEXT,
                time           INTEGER,
                _order         INTEGER
           );
        """
        val intervalTableStr = """
            CREATE TABLE IF NOT EXISTS interval_timer(
                _id            INTEGER PRIMARY KEY AUTOINCREMENT,
                name           TEXT,
                seconds_prepare        INTEGER,
                seconds_Training        INTEGER,
                seconds_rest        INTEGER,
                group_        INTEGER,
                repeat        INTEGER,
                tone           TEXT,
                time          INTEGER,
                _order         INTEGER
            );
        """
        val multiTableStr ="""
           CREATE TABLE IF NOT EXISTS multi_timer(
                _id            INTEGER PRIMARY KEY AUTOINCREMENT,
                name           TEXT,
                content        TEXT,
                time           INTEGER,
                _order         INTEGER
           );
        """

        db?.execSQL(simpleTableStr)
        db?.execSQL(intervalTableStr)
        db?.execSQL(multiTableStr)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}