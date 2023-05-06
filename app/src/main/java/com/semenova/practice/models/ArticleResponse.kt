package com.semenova.practice.models

import com.google.gson.annotations.SerializedName

class ArticleResponse {
    @SerializedName("Id")
    var id: Int = -1

    @SerializedName("Title")
    var title: String = ""

    @SerializedName("Description")
    var description: String = ""

    @SerializedName("FirstName")
    var firstName: String = "firstName"

    @SerializedName("LastName")
    var lastName: String = "lastName"

    @SerializedName("MiddleName")
    var middleName: String = "middleName"

    @SerializedName("Login")
    var login: String = "login"

    @SerializedName("Email")
    var email: String = "email"

    @SerializedName("DateOfPublication")
    var dateOfPublication: String = ""

    @SerializedName("Photo")
    var photo: String = "photo"

    @SerializedName("Gender")
    var gender: String = "gender"
}