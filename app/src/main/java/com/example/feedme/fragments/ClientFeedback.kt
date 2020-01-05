package com.example.feedme.fragments

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClientFeedback(
    @SerializedName("answerId") val answerId: String, @SerializedName("questionId")
    val questionId: String, @SerializedName("roomId") val roomId: String
) : Serializable