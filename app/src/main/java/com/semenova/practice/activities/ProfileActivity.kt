package com.semenova.practice.activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.example.practica.fragment.ArticuleUserFragment
import com.example.practica.fragment.DraftArticleFragment
import com.google.android.material.tabs.TabLayout
import com.semenova.practice.R
import com.semenova.practice.adapters.FragmentAdapter
import com.semenova.practice.converters.BitmapConverter
import com.semenova.practice.models.UserResponse
import com.semenova.practice.untitle.CurrentUser

class ProfileActivity : AppCompatActivity() {
    private lateinit var photoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        photoImageView = findViewById(R.id.photoImageView);
        photoImageView.setImageBitmap(
            when(CurrentUser.user!!.photo) {
                "" -> BitmapFactory.decodeResource(resources, R.drawable.null_photo_icon)
                else -> BitmapConverter.stringToBitmap(CurrentUser.user!!.photo)
            }
        )

        var viewPager = findViewById<ViewPager>(R.id.viewPager)
        var tablayout = findViewById<TabLayout>(R.id.tabLayout)

        val fragmentAdapter = FragmentAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(ArticuleUserFragment(),"Все статьи")
        fragmentAdapter.addFragment(DraftArticleFragment(),"Черновики")

        viewPager.adapter = fragmentAdapter
        tablayout.setupWithViewPager(viewPager)

    }
    fun logout(view: View) {
        CurrentUser.user = UserResponse()
        val editor = getSharedPreferences(PERFORMANCE_HINT_SERVICE, MODE_PRIVATE).edit()
        editor.putString("login", "")
        editor.putString("password", "")
        editor.commit()
        finish()
    }

    fun toEditProfile(view: View) {
        startActivity(Intent(this@ProfileActivity,ProfileEditPage::class.java))
    }

    fun toMain(view: View) {
        val intent = Intent(this, AllArticle::class.java)
        startActivity(intent)
        finish()
    }
}