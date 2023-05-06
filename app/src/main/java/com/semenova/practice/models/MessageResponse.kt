package com.semenova.practice.models

import com.google.gson.annotations.SerializedName

class MessageResponse {
    @SerializedName("message")
    var message: String = ""
}