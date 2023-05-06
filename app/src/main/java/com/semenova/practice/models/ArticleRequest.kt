package com.semenova.practice.models

class ArticleRequest(title: String, description: String, photo: String, date: String = "", user: Int = -1, id: Int = -1) {
    var id: Int = -1
    var title: String = ""
    var description: String = ""
    var dateOfPublication: String = ""
    var photo: String = ""
    var user: Int = -1

    init {
        this.id = id
        this.title = title
        this.description = description
        this.dateOfPublication = date
        this.photo = photo
        this.user = user
    }
}