package com.semenova.practice.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmapOrNull
import com.semenova.practice.R
import com.semenova.practice.api.IApiClient
import com.semenova.practice.models.ArticleRequest
import com.semenova.practice.untitle.CurrentUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Credentials
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewArticleActivity : AppCompatActivity() {
    lateinit var client: IApiClient
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_article)

        client = IApiClient.create(this)
        prefs = getSharedPreferences(PERFORMANCE_HINT_SERVICE, MODE_PRIVATE)
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

    private fun userNotAuthorized() {
        Toast.makeText(
            this@NewArticleActivity,
            "Пользователь неавторизован",
            Toast.LENGTH_LONG
        )
            .show()
    }

    private fun publishArticleFieldUnfilled() {
        Toast.makeText(
            this@NewArticleActivity,
            "Поля не заполнены",
            Toast.LENGTH_LONG
        )
            .show()
    }

    fun publishArticle(view: View) {
        val articleTitle =
            findViewById<EditText>(R.id.editTextTextPersonName3)
                ?.text
                ?.toString()
                ?: return publishArticleFieldUnfilled()

        val articleDescription =
            findViewById<EditText>(R.id.editTextTextMultiLine3)
                ?.text
                ?.toString()
                ?: return publishArticleFieldUnfilled()

        val pictureStream = ByteArrayOutputStream()

        findViewById<ImageView>(R.id.imageView)
            ?.drawable
            ?.toBitmapOrNull()
            ?.compress(Bitmap.CompressFormat.JPEG, 100, pictureStream)

        val pictureByteArray = pictureStream.toByteArray()
        if (pictureByteArray.size >= 2097152) {
            Toast.makeText(
                this@NewArticleActivity,
                "Размер файла превышает 2мб",
                Toast.LENGTH_LONG
            )
                .show()
        }

        val articlePicture = Base64.encodeToString(pictureByteArray, Base64.DEFAULT).replace("\n", "")

        val articleDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)

        val currentUserId = CurrentUser.user.id
        if (currentUserId == -1) {
            return userNotAuthorized()
        }

        CoroutineScope(Dispatchers.IO).launch {
            client
                .createArticle(
                    "AUTH_KEY_NEVER_SET",
                    ArticleRequest(
                        articleTitle,
                        articleDescription,
                        articlePicture,
                        articleDateTime,
                        currentUserId
                    )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Toast.makeText(
                            this@NewArticleActivity,
                            "Статья успешно создана",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        startActivity(
                            Intent(this@NewArticleActivity, AllArticle::class.java))
                    },
                    {
                        Toast.makeText(
                            this@NewArticleActivity,
                            "Неизвестная ошибка: ${it.message}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                )
        }
    }
}