package com.semenova.practice.api

import android.content.Context
import com.semenova.practice.models.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface IApiClient {
    // Article
    @GET("Article/getArticles")
    fun getArticles(): Observable<List<ArticleResponse>>

    @GET("Article/getArticleById{id}/")
    fun getArticleById(@Path("id") id: Int): Observable<List<ArticleResponse>>

    @GET("Article/getArticleByAuthor{search}/")
    fun getArticleByAuthor(@Path("search") name: String): Observable<List<ArticleResponse>>

    @GET("Article/getArticleByDates")
    fun getArticleByDates(
        @Query("dateStart") dateStart: String,
        @Query("dateEnd") dateEnd: String
    ): Observable<List<ArticleResponse>>

    @POST("Article/create")
    fun createArticle(
        @Header("Authorization") authKey: String,
        @Body newArticle: ArticleRequest
    ): Observable<MessageResponse>


    // User
    @GET("User/getUsers")
    fun getUsers(): Observable<List<UserResponse>>

    @GET("User/getUser{token}")
    fun getUserByToken(@Path("token") token: String): Observable<UserResponse>

    @POST("User/login")
    fun userLogin(
        @Query("Login") login: String,
        @Query("Password") password: String
    ): Observable<ResponseBody>

    @POST("User/create")
    fun userRegistration(@Body newUser: UserRequest): Observable<MessageResponse>

    @PUT("User/update{id}")
    fun userUpdate(@Path("id") id: Int, @Body updatedUser: UserRequest): Observable<MessageResponse>

    companion object {
        fun create(context: Context): IApiClient {
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(ApiInterceptor(context))
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl("http://188.164.136.18:8888/api/")
                .build()

            return retrofit.create(IApiClient::class.java)
        }
    }
}