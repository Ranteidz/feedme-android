package com.example.feedme.services

import com.example.feedme.models.Room
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface RoomService {
    @GET("rooms/")
    fun getRooms(
        @Header("x-auth-token") jwt: String
    ): Call<ArrayList<Room>>
}