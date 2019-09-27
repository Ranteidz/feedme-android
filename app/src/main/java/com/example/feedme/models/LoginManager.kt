package com.example.feedme.models

import android.content.SharedPreferences
import android.util.Log
import com.example.feedme.services.LoginService
import com.example.feedme.services.QuestionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginManager(private val sharedPrefs: SharedPreferences, private val retrofit: Retrofit) {

    sealed class JwtResult {
        data class Success(val value: String) : JwtResult()
        data class Failure(val error: Throwable) : JwtResult()
    }

    sealed class LoginResult {
        object Success : LoginResult()
        data class Failure(val error: String) : LoginResult()
    }

    private val TAG = "LoginManager"
    private val JWT_KEY = "com.feedme.prefs.jwt_key"

    init {
        if (sharedPrefs.getString(JWT_KEY, null) != null) {

        } else {
            val editor = sharedPrefs.edit()
            editor.apply()
        }
    }

    fun anonymousLogin(forceCall: Boolean, callback: SimpleCallback) {
        val jwtKey = sharedPrefs.getString(JWT_KEY, null)
        if (jwtKey == null || forceCall) {
            val service = retrofit.create(LoginService::class.java)
            val call = service.anonymousLogin()

            call.enqueue(object : Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i(TAG, t.toString())
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == 200) {
                        callback.onSuccess()
                    } else {
                        callback.onFailure(response.code())
                        Log.e(TAG, response.code().toString())
                    }
                }

            })

        }
    }

    fun getQuestions() {
        val questionService = retrofit.create(QuestionService::class.java)
    }

    fun getJwtToken(): JwtResult {
        val jwt = sharedPrefs.getString(JWT_KEY, null)
        if (jwt != null)
            return JwtResult.Success(jwt)
        return JwtResult.Failure(Error("Hej"))
    }

    interface SimpleCallback {
        fun onSuccess()
        fun onFailure(errorCode: Int)
    }

}
