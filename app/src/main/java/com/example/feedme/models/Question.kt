package com.example.feedme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.*
import java.io.Serializable


data class Question(
    @SerializedName("_id") val _id: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("value") val value: String,
    @SerializedName("answerOptions") val answerOptions: ArrayList<AnswerOption>
) : Serializable