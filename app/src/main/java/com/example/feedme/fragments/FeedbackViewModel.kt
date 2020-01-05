package com.example.feedme.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.feedme.QuestionRecyclerViewAdapter
import com.example.feedme.models.Feedback
import com.example.feedme.models.Question
import com.example.feedme.models.Room
import com.example.feedme.services.FeedbackService
import com.example.feedme.services.QuestionService
import com.example.feedme.services.RetrofitClient
import com.example.feedme.services.RoomService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackViewModel(private val jwt: String) : ViewModel(),
    QuestionRecyclerViewAdapter.QuestionInteractionListener {
    private val TAG = "FeedbackViewModel"
    val liveQuestions = MutableLiveData<List<Question>>()
    val questions = ArrayList<Question>()
    val liveRooms = MutableLiveData<List<Room>>()
    val roomNames = ArrayList<String>()
    var selectedRoom = 0

    fun refresh() {
        fetchRooms()
        val retrofit = RetrofitClient.retrofit
        val questionService = retrofit.create(QuestionService::class.java)
        val call = questionService.getQuestions(jwt, "5e07526f6151b96aacb4a637", true)

        call.enqueue(object : Callback<ArrayList<Question>?> {
            override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
                Log.i(TAG, "HEJJJJJ")
                Log.i(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Question>?>,
                response: Response<ArrayList<Question>?>
            ) {
                val code = response.code()

                val body = response.body()
                Log.i(TAG, "response code: $code \nResponse body: $body")
                questions.clear()
                if (body != null) {
                    questions.addAll(body)
                    liveQuestions.value = response.body()
                }
            }
        })

        Log.i(TAG, "Printed")
        print("HEY")
    }

    fun fetchRooms() {
        val retrofit = RetrofitClient.retrofit
        val roomService = retrofit.create(RoomService::class.java)
        val call = roomService.getRooms(jwt)
        call.enqueue(object : Callback<ArrayList<Room>> {
            override fun onFailure(call: Call<ArrayList<Room>>, t: Throwable) {
                Log.e(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Room>>,
                response: Response<ArrayList<Room>>
            ) {
                val body = response.body()
                val code = response.code()

                if (code == 200 && body != null) {
                    liveRooms.postValue(body)
                    roomNames.clear()
                    for (room in body) {
                        roomNames.add(room.name)
                    }
                }
                Log.i(TAG, "Successful room fetch, BODY: $body and response code: $code")
            }
        })
    }

    override fun onSendClick(position: Int, answer: Int) {
        Log.i(TAG, "YES CLICK $position")
        Log.i(TAG, liveQuestions.value?.get(position)?._id)

        val question = liveQuestions.value!![position]
        val room = liveRooms.value!![selectedRoom]
        val retrofit = RetrofitClient.retrofit
        val feedbackService = retrofit.create(FeedbackService::class.java)
        val answerId = question.answerOptions[answer]._id

        if (answerId != null) {
            val call = feedbackService.createFeedback(
                jwt,
                ClientFeedback(
                    answerId,
                    question._id,
                    room._id
                )
            )
            call.enqueue(object : Callback<Feedback> {
                override fun onFailure(call: Call<Feedback>, t: Throwable) {
                    Log.i(TAG, "FAAAIL")
                }

                override fun onResponse(call: Call<Feedback>, response: Response<Feedback>) {
                    val code = response.code()

                    val body = response.body()
                    if (code == 200 && body != null) {
                        questions.removeIf { q -> q._id == body.question }
                        liveQuestions.postValue(questions)
                    }
                    Log.i(TAG, "response code: $code \nResponse body: $body")
                }
            })
        }
    }



}
