package com.example.feedme.models

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("_id") val _id: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("value") val value: String
)