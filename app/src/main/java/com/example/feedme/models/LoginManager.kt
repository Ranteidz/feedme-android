package com.example.feedme.models

import android.content.SharedPreferences
import android.util.Log
import com.example.feedme.services.LoginService
import com.example.feedme.services.QuestionService
import com.example.feedme.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.Serializable

class LoginManager(private val sharedPrefs: SharedPreferences) {

    sealed class JwtResult {
        data class Success(val value: String) : JwtResult()
        data class Failure(val error: Throwable) : JwtResult()
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
        val retrofit = RetrofitClient.retrofit
        val jwtKey = sharedPrefs.getString(JWT_KEY, null)
        if (jwtKey == null || forceCall) {
            val service = retrofit.create(LoginService::class.java)
            val call = service.anonymousLogin()

            call.enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.i(TAG, t.toString())
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val code = response.code()
                    Log.i(TAG, "JWT: $code")
                    if (code == 200) {
                        val jwt = response.headers()["x-auth-token"]
                        val editor = sharedPrefs.edit()
                        editor.putString(JWT_KEY, jwt)
                        editor.apply()

                        callback.onSuccess()
                    } else {
                        callback.onFailure(response.code())
                        Log.e(TAG, response.code().toString())
                    }
                }

            })

        } else {
            callback.onSuccess()
        }
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
