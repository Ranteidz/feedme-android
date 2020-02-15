package com.example.feedme.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feedme.QuestionRecyclerViewAdapter
import com.example.feedme.models.ClientFeedback
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

    val autoRoomEstimation =  MutableLiveData<Boolean>(true)
    val liveQuestions = MutableLiveData<List<Question>>()
    val questions = ArrayList<Question>()
    val liveRooms = MutableLiveData<List<Room>>()
    val roomNames = ArrayList<String>()
    var selectedRoom = 0
    val liveRoom = MutableLiveData<Room>()

    fun refresh() {
        val retrofit = RetrofitClient.retrofit
        val roomService = retrofit.create(RoomService::class.java)
        fetchRooms(roomService)
        val questionService = retrofit.create(QuestionService::class.java)
        val selectedRoomId = liveRooms.value?.get(selectedRoom)?._id
        if (selectedRoomId != null) {
            val call = questionService.getQuestions(jwt, selectedRoomId)
            call.enqueue(object : Callback<ArrayList<Question>?> {
                override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
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

        }
    }

    fun fetchRooms(roomService: RoomService) {
        val call = roomService.getRooms(jwt)
        call.enqueue(object : Callback<ArrayList<Room>>{
            override fun onFailure(call: Call<ArrayList<Room>>, t: Throwable) {
                Log.e(TAG, "Error retrieving rooms")
            }

            override fun onResponse(
                call: Call<ArrayList<Room>>,
                response: Response<ArrayList<Room>>
            ) {
                Log.i(TAG, response.body().toString())

                val body = response.body()
                if (body != null) {
                    liveRooms.postValue(response.body())
                    roomNames.clear()
                    for (room in body) {
                        roomNames.add(room.name)
                    }
                }
            }
        })
    }

    override fun onAnswer(questionId: String, answerId: String) {
//        Log.i(TAG, "YES CLICK $questionId")
//        Log.i(TAG, liveQuestions.value?.get(questionId)?._id)

//        val question = liveQuestions.value!![questionId]
//        val room = liveRooms.value!![selectedRoom]
        val retrofit = RetrofitClient.retrofit
        val feedbackService = retrofit.create(FeedbackService::class.java)
//        val answerId = question.answerOptions[answerId]._id

        val selectedRoom = liveRoom.value
        if ( selectedRoom != null) {
            val call = feedbackService.createFeedback(
                jwt,
                ClientFeedback(
                    answerId,
                    questionId,
                    selectedRoom._id
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

    fun updateRoom(position: Int) {
        selectedRoom = position
        val selectedRoomId = liveRooms.value?.get(selectedRoom)?._id ?: return

        val retrofit = RetrofitClient.retrofit
        val questionService = retrofit.create(QuestionService::class.java)

        val call = questionService.getQuestions(jwt, selectedRoomId)
            call.enqueue(object : Callback<ArrayList<Question>?> {
                override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
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
    }

    fun onNewRoomEstimated(room: Room) {
        autoRoomEstimation.value?.let {
            if (it) {
                val position = roomNames.indexOf(room.name)
                if (position == -1){
                    liveRooms.postValue(arrayListOf(room))
                    selectedRoom = 0
                    roomNames.clear()
                    roomNames.add(room.name)
                    liveRoom.postValue(room)

                    val retrofit = RetrofitClient.retrofit
                    val questionService = retrofit.create(QuestionService::class.java)

                    val call = questionService.getQuestions(jwt, room._id)
                    call.enqueue(object : Callback<ArrayList<Question>?> {
                        override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
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
                }
                else
                    updateRoom(position)
            }
        }

    }

    class FeedbackViewModelFactory(private val jwt: String) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedbackViewModel(jwt) as T
        }
    }

    companion object {
        private val TAG = "FeedbackViewModel"
    }

}
