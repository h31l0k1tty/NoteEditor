package com.semenova.practice.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.semenova.practice.R
import com.semenova.practice.models.UserResponse


class UserAdapter(context: Context, resource: Int, authores: List<UserResponse>) :
    ArrayAdapter<UserResponse?>(context, resource, authores) {
    private val inflater: LayoutInflater
    private val layout: Int
    private val authores: List<UserResponse>

    init {
        this.authores = authores
        layout = resource
        inflater = LayoutInflater.from(context)
    }
    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val fullName = view.findViewById<TextView>(R.id.fullNameTextView)
        val quantity = view.findViewById<TextView>(R.id.quantityTextView)
        val author: UserResponse = authores[position]
        fullName.text = author.firstName + " " + author.lastName + " " + author.middleName
        quantity.text = //
        return view
    }
}