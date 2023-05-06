package com.semenova.practice.api

import android.content.Context
import com.semenova.practice.exceptions.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiInterceptor(context: Context) : Interceptor {
    private var context: Context

    init {
        this.context = context
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isOnline(context)) {
            throw NoConnectivityException()
        }

        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}