package com.semenova.practice.exceptions

import java.io.IOException


class NoConnectivityException : IOException() {
    override val message: String
        get() = "Нет соединения с интернетом"
}