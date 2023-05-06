package com.semenova.practice.activities

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.semenova.practice.R
import java.util.*

class LoadingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)
        val sdf = SimpleDateFormat("yyyy-M-d hh:mm:ss")
        findViewById<TextView>(R.id.dateTimeTextView).text = sdf.format(Date())
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@LoadingScreenActivity, AllArticle::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}