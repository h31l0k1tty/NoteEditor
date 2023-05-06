package com.semenova.practice.api

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.semenova.practice.models.ArticleRequest

class Database(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?,
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, 2) {

    override fun onCreate(p0: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE IF NOT EXISTS article(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, photo TEXT);")

        p0?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS " + "profile");
        p0?.execSQL("DROP TABLE IF EXISTS " + "article");
        onCreate(p0)
    }

    fun addProfile(login: String, password: String) {

        val values = ContentValues()

        values.put("login", login)
        values.put("password", password)

        val db = this.writableDatabase

        // all values are inserted into database
        db.insert("profile", null, values)

        db.close()
    }

    fun addArticle(article: ArticleRequest) {

        val values = ContentValues()

        values.put("title", article.title)
        values.put("description", article.description)
        values.put("photo", article.photo)

        val db = this.writableDatabase

        db.insert("article", null, values)

        db.close()
    }

    fun getArticles(): ArrayList<ArticleRequest> {

        val articles = ArrayList<ArticleRequest>()

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM article", null)

        while (cursor.moveToNext()) {

            val article = ArticleRequest(
                id = cursor.getInt(0),
                title = cursor.getString(1),
                description = cursor.getString(2),
                photo = cursor.getString(3)
            )
            articles.add(article)
        }

        cursor.close()

        return articles
    }

    fun getArticle(id: String): ArticleRequest? {

        var article: ArticleRequest? = null

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM article WHERE id = $id", null)

        if (cursor.moveToFirst()) {
            article = ArticleRequest(
                id = cursor.getInt(0),
                title = cursor.getString(1),
                description = cursor.getString(2),
                photo = cursor.getString(3)
            )
        }

        cursor.close()

        return article
    }

    fun deleteArticle(id: Int) {
        val db = this.writableDatabase

        db.execSQL("DELETE from article where id = $id")
    }

    fun updateArticle(id: Int, article: ArticleRequest) {
        val values = ContentValues()

        values.put("title", article.title)
        values.put("description", article.description)
        values.put("photo", article.photo)

        val db = this.writableDatabase

        db.update(
            "article",
            values,
            "id" + "= ?", arrayOf("$id")
        )

        db.close()
    }

    companion object {
        private val DATABASE_NAME = "practice"

//        private val DATABASE_VERSION = 1
//
//        val PROFILE_TABLE = "profile"
//
//        val ID_COL = "id"
//
//        val LOGIN_COL = "login"
//        val PASSWORD_COL = "password"
//
//        val TITLE_COL = "title"
//        val DESCRIPTION_COL = "description"
//        val PHOTO_COL = "photo"
    }
}