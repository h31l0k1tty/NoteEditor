package com.semenova.practice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.semenova.practice.R

class NewArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_article)
    }

    fun toAllArticles(view: View){
        startActivity(Intent(this, AllArticle::class.java))
    }

    private val setPicture = registerForActivityResult(ActivityResultContracts.GetContent()) {
        findViewById<ImageView>(R.id.imageView).setImageURI(it)
    }

    fun getPicture(view: View) {
        setPicture.launch("image/*")
    }
}