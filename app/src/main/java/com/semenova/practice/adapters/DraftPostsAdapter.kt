package com.semenova.practice.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.semenova.practice.R
import com.semenova.practice.models.ArticleRequest


class DraftPostsAdapter(context: Context, resource: Int, articles: List<ArticleRequest>) :
    ArrayAdapter<ArticleRequest?>(context, resource, articles) {
    private val inflater: LayoutInflater
    private val layout: Int
    private val articles: List<ArticleRequest>

    init {
        this.articles = articles
        this.layout = resource
        this.inflater = LayoutInflater.from(context)
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
        val dateOfPublication = view.findViewById<TextView>(R.id.dateOfPublicationTextView)
        val title = view.findViewById<TextView>(R.id.titleTextView)
        val description = view.findViewById<TextView>(R.id.descriptionTextView)
        val photo = view.findViewById<ImageView>(R.id.photoImageView)
        val article: ArticleRequest = articles[position]
        dateOfPublication.text = article.dateOfPublication
        title.text = article.title
        var str = ""
        if(article.description.length > 200){
            for(i in 0..199){
                str += article.description[i]
            }
            str += "..."
        }
        else{
            str = article.description
        }
        description.text = str
        if(article.photo == ""){
            photo.setImageResource(R.drawable.null_photo_icon);
        }
        else{
            photo.setImageBitmap(convert(article!!.photo)) // Мб стоит поменять
        }
        return view
    }
}