package com.semenova.practice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.semenova.practice.R
import com.semenova.practice.adapters.PostsAdapter
import com.semenova.practice.api.IApiClient
import com.semenova.practice.models.ArticleResponse
import com.semenova.practice.untitle.CurrentUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AllArticle : AppCompatActivity() {
    private val client by lazy {
        IApiClient.create(this@AllArticle)
    }
    lateinit var articleAdapter: PostsAdapter
    private lateinit var articleList: List<ArticleResponse>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_article)
        val checkedUp = findViewById<RadioButton>(R.id.upRadioButton)
        checkedUp.isChecked = true
        var checkedSort: Boolean = false
        val button = findViewById<ImageView>(R.id.profileImageView)
        var articleListView = findViewById<ListView>(R.id.ArticlesListView)
        val search = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<ImageView>(R.id.searchImageView)
        val dateButton = findViewById<ImageView>(R.id.dateImageView)
        val filter = findViewById<EditText>(R.id.editTextDate)
        val writetext:TextView = findViewById(R.id.write_atricle_text)
        val writeButton:Button = findViewById(R.id.write_atricle_button)
        fun Loaded(sort: Boolean){
            CoroutineScope(Dispatchers.IO).launch {
                client.getArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                            result -> articleList = result
                        if(sort == true){
                            articleAdapter = PostsAdapter(this@AllArticle, R.layout.article_item, articleList)
                        }
                        else{
                            var newArticleList = articleList.sortedByDescending { it.dateOfPublication }
                            articleAdapter = PostsAdapter(this@AllArticle, R.layout.article_item, newArticleList)
                        }
                        if(search.text.isBlank() != true){
                            articleList = articleList.filter{it.description.contains(search.text) ||
                                    it.title.contains(search.text) ||
                                    it.firstName.contains(search.text)
                            }
                            articleAdapter = PostsAdapter(this@AllArticle, R.layout.article_item, articleList)
                        }
                        if(filter.text.isBlank() != true){
                            articleList = articleList.filter{it.dateOfPublication.contains(filter.text)
                            }
                            articleAdapter = PostsAdapter(this@AllArticle, R.layout.article_item, articleList)
                        }
                        if(articleList.count() == 0){
                            val article: ArticleResponse = ArticleResponse();
                            article.title = "Результатов нет"
                            article.photo = " "
                            var newArticleList: ArrayList<ArticleResponse> = ArrayList<ArticleResponse>()
                            newArticleList.add(article)
                            articleList = newArticleList
                            articleAdapter = PostsAdapter(this@AllArticle, R.layout.article_item, articleList)
                        }
                        articleListView.setAdapter(articleAdapter)
                    },
                        { error ->
                            var errorMessage: String = ""
                            errorMessage = when ((error as HttpException).code()) {
                                401 -> "Для этого действия необходимо авторизоваться"
                                403 -> "Это действие невозможно выполнить"
                                else -> "Неизвестная ошибка"
                            }
                            Toast.makeText(this@AllArticle, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    )
            }
        }
        Loaded(false)
        checkedUp.setOnCheckedChangeListener{ buttonView, isChecked ->
            checkedSort = !isChecked
            Loaded(checkedSort)
        }
        searchButton.setOnClickListener{
            Loaded(checkedSort)
        }
        dateButton.setOnClickListener{
            Loaded(checkedSort)
        }
        button.setOnClickListener{
            val intent =
                if (CurrentUser.user.id!=-1) Intent(this, ProfileActivity::class.java)
                else Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
        }

        if (CurrentUser.user.id==-1) {

            writeButton.visibility = View.INVISIBLE
            writetext.visibility = View.INVISIBLE
        }
        else {
            writeButton.visibility = View.VISIBLE
            writetext.visibility = View.VISIBLE
        }

    }
    fun toNewArticle(view: View){
        val i = Intent(this, NewArticleActivity::class.java)
        startActivity(i)
    }
}