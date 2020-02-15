package com.example.feedme.services

import com.example.feedme.models.SignalMapRequest
import com.example.feedme.models.SignalMapResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SignalMapService {
    @POST("signalMaps/")
    fun estimateRoom(@Header("x-auth-token") authToken: String,
                     @Body signalMapRequest: SignalMapRequest
    ): Call<SignalMapResponse>
}
