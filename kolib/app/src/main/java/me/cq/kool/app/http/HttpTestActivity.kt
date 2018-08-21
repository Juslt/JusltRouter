package me.cq.kool.app.http

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import me.cq.kool.app.R
import me.cq.kool.http.hGet
import me.cq.kool.http.hPost
import me.cq.kool.http.parse
import me.cq.kool.log.debug
import java.io.File


/**
 * Created by phelps on 2018/1/20 0020.
 */
class HttpTestActivity : AppCompatActivity(){

    private val ll by lazy { findViewById<LinearLayout>(R.id.ll) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_test)

//        hGet()
        hPost()
//        hPostMultipart()
    }

    private fun hGet(){
        Thread {

            (0..100).forEach {
                val response = "http://10.0.2.2:8080/a".hGet().exec()
                runOnUiThread {
                    addLog("code="+response.responseCode+" --> "+response.data)
                }

            }
        }.start()
    }

    private fun hPost(){
        Thread{

            Thread{
//                (0..100).forEach {
                    val response = "http://39.108.178.70:6070/checkVersion".hPost()
                            .addParam("channelID" to 1)
                            .addParam("versionNum" to "1.0.0")
                            .addParam("sign" to "a8e1cb17f3b18ef0202b49996dce2650")
                            .exec(true)
                    debug(response.toString())

                    runOnUiThread {
                        addLog("code="+response.responseCode+" --> "+response.data)
                    }
//                }
            }.start()

        }.start()
    }


    private fun hPostMultipart(){
        Thread{
//                (0..100).forEach {
                    val response = "http://39.108.178.70:6070/userSubmitAutoArticle".hPost()
                            .addParam("phoneNumber" to "18507141455")
                            .addParam("taskID" to "5")
                            .addParam("title" to "测试自主任务西瓜视频1")
                            .addParam("content" to "测试内容")
                            .addParam("contentDocFile" to File("/storage/emulated/0/Download/e0d2a48baade79dcce1aa09dd1b1f5e7.jpeg"))
                            .addParam("sign" to "b2a6c5a69ddb760de475cd4b9eb76032")
                            .exec()

                    val result = response.parse(Result::class.java)

//                    val response = "http://10.0.2.2:8080/form".hPost()
//                            .addParam("test" to "测试参数")
//                            .addParam("testFile" to File("/storage/emulated/0/Download/e0d2a48baade79dcce1aa09dd1b1f5e7.jpeg"))
//                            .exec()
                    runOnUiThread {

//                        addLog("code="+response.responseCode+" --> "+response.data)
                        addLog("code="+response.responseCode+" --> "+result.toString())
                    }
//                }
        }.start()
    }

    private fun addLog(log: String){
        val tv = TextView(this)
        tv.setSingleLine(true)
        tv.text = log
        ll.addView(tv)
    }

    data class Result(val retCode: Int=0)
}