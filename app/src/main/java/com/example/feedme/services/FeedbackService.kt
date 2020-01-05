package com.example.feedme.services

import com.example.feedme.fragments.ClientFeedback
import com.example.feedme.models.Feedback
import retrofit2.Call
import retrofit2.http.*

interface FeedbackService {
    @POST("feedback/")
    fun createFeedback(
        @Header("x-auth-token") jwt: String,
        @Body feedback: ClientFeedback
    ): Call<Feedback>
}