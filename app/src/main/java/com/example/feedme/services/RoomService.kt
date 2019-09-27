package com.example.feedme.services

import retrofit2.http.GET

interface RoomService {
    @GET("rooms")
    fun getRooms(jwt: String)
}