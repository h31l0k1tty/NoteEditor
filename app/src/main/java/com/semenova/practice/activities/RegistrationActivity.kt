package com.semenova.practice.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.content.DialogInterface
import android.provider.MediaStore
import com.semenova.practice.R
import com.semenova.practice.api.IApiClient
import com.semenova.practice.models.GenderEnum
import com.semenova.practice.models.RoleEnum
import com.semenova.practice.models.UserRequest


class RegistrationActivity : AppCompatActivity() {

    lateinit var lastnameView: EditText
    lateinit var firstnameView: EditText
    lateinit var middlenameView: EditText
    lateinit var dateView: EditText
    lateinit var loginView: EditText
    lateinit var emailView: EditText
    lateinit var passwordView: EditText
    lateinit var maleButton:RadioButton
    var photo:String = ""
    lateinit var imageView: ImageView
    var photoBytes:ByteArray = byteArrayOf()
    lateinit var client: IApiClient
    val filePickType:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        lastnameView = findViewById(R.id.regLastnameText)
        firstnameView = findViewById(R.id.regFirstnameText)
        middlenameView = findViewById(R.id.regMiddlenameText)
        dateView = findViewById(R.id.regDateOfBirthText)
        loginView = findViewById(R.id.regLoginText)
        emailView = findViewById(R.id.regEmailText)
        passwordView = findViewById(R.id.regPasswordText)
        maleButton = findViewById(R.id.regMaleButton)
        client = IApiClient.create(this)
    }
    fun toAuth(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение")
            .setMessage("Вы уверены, что хотите отменить регистрацию?")
            .setPositiveButton("Да",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this, AuthorizationActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            .setNegativeButton("Нет",
                { dialog, id ->

                })
        builder.create().show()

    }
    fun redactPhoto(view: View) {
        getPhoto()
    }

    private fun isNotBlank():Boolean {
        return lastnameView.text.toString()!=""&&
                middlenameView.text.toString()!=""&&
                firstnameView.text.toString()!=""&&
                dateView.text.toString()!=""&&
                loginView.text.toString()!=""&&
                emailView.text.toString()!=""&&
                passwordView.text.toString()!=""
    }
    override fun onActivityResult(requstCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requstCode, resultCode, data)
        if (requstCode == filePickType && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            val uri = data.data
            imageView = findViewById(R.id.reg_imageview)
            val stream = ByteArrayOutputStream()
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bytes = stream.toByteArray()
            if (checkPhotoSize(bytes)) {
                photoBytes = bytes
                imageView.setImageBitmap(bitmap)

            } else
                Toast.makeText(
                    this,
                    "Размер изображения не должен превышать 2МБ",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun getPhoto() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, filePickType)
    }
    fun makeUser(): UserRequest {
        if (photoBytes != null)
            photo =  Base64.encodeToString(photoBytes, Base64.DEFAULT).replace("\n", "")
        val maleButton = findViewById<RadioButton>(R.id.regMaleButton)
        var gender = GenderEnum.Female
        if (maleButton.isChecked)
            gender = GenderEnum.Male
        return UserRequest(
            firstnameView.text.toString(),
            lastnameView.text.toString(), middlenameView.text.toString(),
            loginView.text.toString(), passwordView.text.toString(),
            emailView.text.toString(),
            dateView.text.toString(), RoleEnum.User,
            gender, photo
        )
    }
    private fun checkPhotoSize(photo:ByteArray):Boolean {
        Toast.makeText(this, photo.size.toString(), Toast.LENGTH_LONG).show()
        return photo.size<2097152
    }
    private fun correctEmail(): Boolean {
        return android.util.Patterns.
        EMAIL_ADDRESS.matcher(emailView.text.toString()).matches()

    }
    private fun correctPassword():Boolean {
        return passwordView.text.length in 6..15
    }
    fun registrate(view: View) {
        val newUser = makeUser()
        if (isNotBlank()) {
            if (correctEmail()&&correctPassword()) {
                val q = CoroutineScope(Dispatchers.IO).launch {
                    client.userRegistration(newUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { _ ->
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Пользователь успешно создан",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                val intent =
                                    Intent(
                                        this@RegistrationActivity,
                                        AuthorizationActivity::class.java
                                    )
                                startActivity(intent)
                            },
                            { errors ->
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Неизвестная ошибка: ${errors.message}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        )
                }
            }
            else
                Toast.makeText(this, "Неверный формат данных", Toast.LENGTH_LONG).show()

        }
        else
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
    }
}