package com.example.feedme.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Feedback(
    @SerializedName("question") val question: String,
    @SerializedName("user") val user: String,
    @SerializedName("room") val room: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("timesAnswered") val timesAnswered: Int
) : Serializable