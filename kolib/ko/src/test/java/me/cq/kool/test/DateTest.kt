package me.cq.kool.test

import me.cq.kool.time.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Created by phelps on 2018/1/24 0024.
 */
class DateTest{

    @Test
    fun testPlus(){


        val d1 = Date()
        d1.year = 2018-1900
        d1.month = 2
        d1.date = 10
        d1.hours = 12
        d1.minutes = 30
        d1.seconds = 40

        val d2 = d1 + TimeUnit.YEAR *2

        val d3 = d1 + TimeUnit.YEAR *2 +
                TimeUnit.MONTH*2 +
                TimeUnit.WEEK +
                TimeUnit.DAY +
                TimeUnit.HOUR*4 +
                TimeUnit.MINUTE*5 +
                TimeUnit.SECOND*6

        val d4 = d1 - TimeUnit.YEAR - TimeUnit.YEAR
        val d5 = d1 + TimeUnit.YEAR *-2
        val d6 = d1 - TimeUnit.YEAR *2

        assertEquals("20200310123040",d2.yyyyMMddHHmmss)
        assertEquals("20200518163546",d3.yyyyMMddHHmmss)
        assertEquals(d4.yyyyMMddHHmmss,d5.yyyyMMddHHmmss)
        assertEquals(d6.yyyyMMddHHmmss,d5.yyyyMMddHHmmss)
    }

    @Test
    fun testLoop(){

        val d1 = Date()
        val d2 = d1 + TimeUnit.WEEK

        for (d in d1..d2){
            println(d.yyyyMMddHHmmss)
        }

    }

}