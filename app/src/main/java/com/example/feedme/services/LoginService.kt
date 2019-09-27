package com.example.feedme.services

import com.example.feedme.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {
    @POST("users")
    fun anonymousLogin() : Call<String>
}