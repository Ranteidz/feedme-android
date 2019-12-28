package com.example.feedme.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.view.View
import com.example.feedme.QuestionRecyclerViewAdapter
import com.example.feedme.R
import com.example.feedme.models.Question
import com.example.feedme.services.QuestionService
import com.example.feedme.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedbackViewModel : ViewModel(), QuestionRecyclerViewAdapter.QuestionInteractionListener {

    private val TAG = "FeedbackViewModel"
    val questions = MutableLiveData<List<Question>>()


    fun refresh(jwt: String) {
        val retrofit = RetrofitClient.retrofit
        val questionService = retrofit.create(QuestionService::class.java)
        val call = questionService.getQuestions(jwt, "5e07526f6151b96aacb4a637")
        call.enqueue(object : Callback<ArrayList<Question>?> {
            override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
                Log.i(TAG, "HEJJJJJ")
                Log.i(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Question>?>,
                response: Response<ArrayList<Question>?>
            ) {
                val message = response.message()
                val code = response.code()

                Log.i(TAG, "response code: $code \nWith message: $message")
                questions.value = response.body()
            }
        })

        Log.i(TAG, "Printed")
        print("HEY")
    }

    override fun onYesClick(position: Int) {
        Log.i(TAG, "YES CLICK $position")
        Log.i(TAG, questions.value?.get(position)?._id)
    }

    override fun onNoClick(position: Int) {
        Log.i(TAG, "NO CLICK $position")

    }
}
