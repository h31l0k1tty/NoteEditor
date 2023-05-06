package com.semenova.practice.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.semenova.practice.R
import com.semenova.practice.api.Database
import com.semenova.practice.api.IApiClient

class MainActivity : AppCompatActivity() {
    private val client by lazy {
        IApiClient.create(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Database(this@MainActivity, null)
    }
}