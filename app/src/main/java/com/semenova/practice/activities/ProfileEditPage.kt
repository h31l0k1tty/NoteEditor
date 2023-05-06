package com.semenova.practice.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.semenova.practice.R
import com.semenova.practice.api.IApiClient
import com.semenova.practice.converters.BitmapConverter
import com.semenova.practice.exceptions.ErrorHandling
import com.semenova.practice.models.GenderEnum
import com.semenova.practice.models.RoleEnum
import com.semenova.practice.models.UserRequest
import com.semenova.practice.models.UserResponse
import com.semenova.practice.untitle.CurrentUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileEditPage : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var emailNameEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var photoImageView: ImageView
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private var photoBitmap: Bitmap? = null
    private lateinit var lastUser: UserResponse;
    lateinit var client: IApiClient

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_page)
        client = IApiClient.create(this)
        lastUser = CurrentUser.user

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        middleNameEditText = findViewById(R.id.middleNameEditText)
        emailNameEditText = findViewById(R.id.emailEditText)
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        photoImageView = findViewById(R.id.photoImageView)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)

        firstNameEditText.setText(lastUser.firstName)
        lastNameEditText.setText(lastUser.lastName)
        middleNameEditText.setText(lastUser.middleName)
        emailNameEditText.setText(lastUser.email)
        dateOfBirthEditText.setText(lastUser.dateOfBirth.split("T")[0])
        photoBitmap = BitmapConverter.stringToBitmap(lastUser.photo)
        photoImageView.setImageBitmap(
            when(lastUser.photo) {
                "" -> BitmapFactory.decodeResource(resources, R.drawable.null_photo_icon)
                else -> BitmapConverter.stringToBitmap(lastUser.photo)
            }
        )

    }
    fun saveChanges(view: View) {
        val firstName = firstNameEditText.text.trim().toString();
        val lastName = lastNameEditText.text.trim().toString();
        val middleName = middleNameEditText.text.trim().toString();
        val emailName = emailNameEditText.text.trim().toString();
        val password = newPasswordEditText.text.trim().toString()
        val confirmPassword = confirmPasswordEditText.text.trim().toString()
        val login = lastUser.login
        val dateOfBirth = dateOfBirthEditText.text.trim().toString()
        val gender = if (lastUser.gender == "Male") GenderEnum.Male; else GenderEnum.Female
        val role = RoleEnum.User;
        val photo = BitmapConverter.bitmapToString(
            when (photoBitmap) {
                null -> BitmapFactory.decodeResource(resources, R.drawable.null_photo_icon)
                else -> photoBitmap
            }
        )
        val user = UserRequest(firstName, lastName, middleName, login, password, emailName, dateOfBirth+ "T00:00:00", role, gender, photo, confirmPassword)
        if (password.length < 6) {
            Toast.makeText(this@ProfileEditPage, "Пароль должен быть не меньше 6 символов", Toast.LENGTH_LONG).show()
            return
        }
        if (firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty() || emailName.isEmpty() || password.isEmpty() || login.isEmpty() || dateOfBirth.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this@ProfileEditPage, "Заполните все поля", Toast.LENGTH_LONG).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this@ProfileEditPage, "Пароли не совпадают", Toast.LENGTH_LONG).show()
            return
        }
        if (!correctEmail(emailName)) {
            Toast.makeText(this@ProfileEditPage, "E-mail не корректный", Toast.LENGTH_LONG).show()
            return
        }
        if (!correctPassword(password)) {
            Toast.makeText(this@ProfileEditPage, "Пароль должен быть размером от 6 до 15 символов", Toast.LENGTH_LONG).show()
            return
        }
        if (!dateParse(dateOfBirth)) {
            Toast.makeText(this@ProfileEditPage, "Неверный формат даты", Toast.LENGTH_LONG).show()
            return
        }
        if (emailName==lastUser.email) {
            Toast.makeText(this@ProfileEditPage, "E-mail уже используется", Toast.LENGTH_LONG).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            client.getArticleByAuthor(lastUser.login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result.isEmpty())
                            client.userUpdate(lastUser.id, user)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { result ->
                                        Toast.makeText(
                                            this@ProfileEditPage,
                                            result.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    { error ->
                                        Toast.makeText(this@ProfileEditPage, ErrorHandling.requestError(error), Toast.LENGTH_LONG).show()
                                    }
                                )
                        else
                            Toast.makeText(this@ProfileEditPage, "Невозможно изменить пользователя с опубликованными постами", Toast.LENGTH_LONG).show()
                    },
                    { error ->
                        Toast.makeText(this@ProfileEditPage, ErrorHandling.requestError(error), Toast.LENGTH_LONG).show()
                    }
                )
        }
    }
    fun dateParse(dateStr: String):Boolean {
        val formatter = SimpleDateFormat("yyyy-mmm-dd", Locale.getDefault())
        return try {
            val date = formatter.parse(dateStr)
            true
        }catch(ex :java.lang.Exception) {
            false
        }
    }
    private fun correctEmail(email:String): Boolean {
        return android.util.Patterns.
        EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun correctPassword(password:String):Boolean {
        return password.length in 6..15
    }
    fun getImage(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, BitmapConverter.REQUEST_TAKE_PHOTO)
    }
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            photoBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            photoImageView.setImageBitmap(photoBitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    fun pdfGenerate(view: View) {
        var user = lastUser
        val qrBitmap = BitmapConverter.getQrCodeBitmap("${user?.middleName} ${user?.firstName} ${user.lastName} ${user.dateOfBirth.split("T")[0]} ${user.gender}")
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.setTextSize(30F)
        title.color = ContextCompat.getColor(this@ProfileEditPage, R.color.black)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.color = ContextCompat.getColor(this@ProfileEditPage, R.color.black)
        var photoScale = Bitmap.createScaledBitmap(photoBitmap!!, photoBitmap!!.width/2, photoBitmap!!.height/2, false)
        val height = if(photoScale.width > 400) photoScale.width else photoScale.width*2
        val myPageInfo = PdfDocument.PageInfo.Builder(height, photoScale.height+1000, 1).create()
        val myPage = pdfDocument.startPage(myPageInfo)
        val canvas = myPage.canvas
        canvas.drawBitmap(photoScale, 0F, 10F, paint)
        canvas.drawText("ФИО: ${user.middleName}.${user.firstName[0]}.${user.lastName[0]}", 10F, photoScale.height+30F, title);
        canvas.drawText("E-mail: ${user.email}", 10F, photoScale.height+65F, title);
        canvas.drawText("Пол: ${user.gender}", 10F, photoScale.height+95F, title);
        canvas.drawText("Роль: ${user.role}", 10F, photoScale.height+125F, title);
        canvas.drawBitmap(qrBitmap, 0F, photoScale.height+130F, paint)
        pdfDocument.finishPage(myPage)
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + lastUser.login+".pdf")
        try {
            file.createNewFile()
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this@ProfileEditPage,"PDF успешно создан.",Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this@ProfileEditPage,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }

    fun goBack(view: View) {
        finish();
    }
}