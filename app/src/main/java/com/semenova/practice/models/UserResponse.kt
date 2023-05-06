package com.semenova.practice.models

import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("id")
    var id: Int = -1

    @SerializedName("firstName")
    var firstName: String = ""

    @SerializedName("lastName")
    var lastName: String = ""

    @SerializedName("middleName")
    var middleName: String = ""

    @SerializedName("login")
    var login: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("dateOfBirth")
    var dateOfBirth: String = ""

    @SerializedName("photo")
    var photo: String = ""

    @SerializedName("role")
    var role: String = ""

    @SerializedName("gender")
    var gender: String = ""
}