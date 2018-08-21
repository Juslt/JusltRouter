package me.cq.kool.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import me.cq.kool.app.http.HttpTestActivity
import me.cq.kool.app.ios.IOSWidgetActivity
import me.cq.kool.app.novel.NovelActivity

class MainActivity : AppCompatActivity() {

    val btnNovel by lazy { findViewById<Button>(R.id.btn_novel) }
    val btnIos by lazy { findViewById<Button>(R.id.btn_ios) }
    val btnHttp by lazy { findViewById<Button>(R.id.btn_http) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNovel.setOnClickListener {
            startActivity(Intent(this,NovelActivity::class.java))
        }

        btnIos.setOnClickListener {
            startActivity(Intent(this,IOSWidgetActivity::class.java))
        }

        btnHttp.setOnClickListener {
            startActivity(Intent(this,HttpTestActivity::class.java))
        }

    }
}
