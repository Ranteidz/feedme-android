package com.example.feedme.services

import com.example.feedme.models.Question
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface QuestionService {
    @GET("questions")
    fun getQuestions(@Header("x-auth-token") authToken: String,
                     @Header("roomId") roomId: String,
                     @Query("notAnswered") notAnswered: Boolean
    ): Call<ArrayList<Question>>
}