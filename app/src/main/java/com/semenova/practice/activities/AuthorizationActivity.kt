package com.semenova.practice.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.semenova.practice.R
import com.semenova.practice.api.IApiClient
import com.semenova.practice.untitle.CurrentUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthorizationActivity : AppCompatActivity() {
    lateinit var loginView:EditText
    lateinit var passwordView:EditText
    lateinit var rememberMeCheckBox:CheckBox
    lateinit var prefs:SharedPreferences
    lateinit var client: IApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        client = IApiClient.create(this)
        loginView = findViewById(R.id.authLoginText)
        prefs =  getSharedPreferences(PERFORMANCE_HINT_SERVICE, MODE_PRIVATE)

        passwordView = findViewById(R.id.authPasswordText)
        rememberMeCheckBox = findViewById(R.id.rememberMeSwitch)
        val login = prefs.getString("login", "")
        val password = prefs.getString("password", "")
        if (login!="") {
            passwordView.text.insert(0, password)
            loginView.text.insert(0, login)
        }
    }
    fun toMain(view: View) {
        val intent = Intent(this, AllArticle::class.java)
        startActivity(intent)
        finish()
    }
    fun login(view: View) {
        var token:String? = ""
        //val intent = Intent(this, MainActivity::class.java)
        val login = loginView.text.toString()
        val passwword = passwordView.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            client.userLogin(
                login, passwword

            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        token = result?.string()
                        putUser(token)
                        if (rememberMeCheckBox.isChecked){
                            val editor =prefs.edit()
                            editor.putString("login", login)
                            editor.putString("password", passwword)
                            editor.commit()
                        }
                        else {
                            val editor = prefs.edit()
                            editor.putString("login", "")
                            editor.putString("password", "")
                            editor.commit()
                        }

                    },
                    { error ->
                        var errorMessage: String = ""
                        errorMessage = when (error) {
                            is HttpException ->{
                                when (error.code()) {
                                    401 ->"Необходимо авторизоваться"
                                    403-> "Это действие нельзя выполнить"
                                    else -> "Неизвестная ошибка"
                                }
                            }
                            else -> {
                                error.localizedMessage
                            }
                        }
                        println(errorMessage)
                        Toast.makeText(this@AuthorizationActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
        }
    }

    private fun putUser(token: String?) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                client.getUserByToken(
                    token.toString()
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            CurrentUser.user = result
                            println(CurrentUser.user.id)
                            val intent = Intent(this@AuthorizationActivity, AllArticle::class.java)
                            startActivity(intent)
                        },
                        { error ->
                            var errorMessage: String = ""
                            errorMessage = when (error) {
                                is HttpException -> {
                                    when (error.code()) {
                                        401 -> "Необходимо авторизоваться"
                                        403 -> "Это действие нельзя выполнить"
                                        else -> "Неизвестная ошибка"
                                    }
                                }
                                else -> {
                                    error.localizedMessage
                                }
                            }
                            Toast.makeText(
                                this@AuthorizationActivity,
                                errorMessage,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    )
            }
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }

    fun registrationOnClick(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }
}