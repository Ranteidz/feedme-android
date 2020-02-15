package com.example.feedme.services

interface SimpleCallback<T> {
    fun onSuccess(body: T)
    fun onError(statusCode: Int, message: String)
}