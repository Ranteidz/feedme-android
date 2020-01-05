package com.example.feedme.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Room (
    @SerializedName("_id") val _id: String,
    @SerializedName("name") val name: String,
    @SerializedName("building") val building: String
) : Serializable