package com.example.feedme.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feedme.QuestionRecyclerViewAdapter
import com.example.feedme.databinding.FeedbackFragmentBinding
import com.example.feedme.models.LoginManager
import com.example.feedme.models.Question
import com.example.feedme.models.Room
import com.example.feedme.services.RetrofitClient
import com.example.feedme.services.RoomService
import com.example.feedme.viewmodels.FeedbackViewModel

class FeedbackFragment : Fragment() {
    companion object {
        const val TAG = "FeedbackFragment"
        fun newInstance() = FeedbackFragment()
    }

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewModel: FeedbackViewModel
    private lateinit var binding: FeedbackFragmentBinding
    private lateinit var questionsAdapter: QuestionRecyclerViewAdapter
    private lateinit var jwt: String
    private lateinit var spinner: Spinner
//    private lateinit var roomTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        jwt = arguments?.getString("jwt")!!
        binding = FeedbackFragmentBinding.inflate(inflater, container, false)

        questionsAdapter = QuestionRecyclerViewAdapter()
        binding.questionsListView.layoutManager = LinearLayoutManager(context)
        binding.questionsListView.adapter = questionsAdapter
//        spinner = binding.spinner
//        roomTextView = binding.roomTxtView

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, FeedbackViewModel.FeedbackViewModelFactory(jwt))
            .get(FeedbackViewModel::class.java)
        viewModel.liveQuestions.observe(
            viewLifecycleOwner,
            Observer {
                questionsAdapter.setQuestionsList(it, viewModel)
            })
       /* val adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_item,
            viewModel.roomNames
        )*/

//        spinner.adapter = adapter
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

//        viewModel.liveRooms.observe(viewLifecycleOwner, Observer {
//            adapter.notifyDataSetChanged()
//        })

//        viewModel.liveRoom.observe(viewLifecycleOwner, Observer {
//            roomTextView.text = it.name
//        })

//        spinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Toast.makeText(context, "ØV ", Toast.LENGTH_LONG).show()
//                }
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    viewModel.updateRoom(position)
//
//                }
//
//            }
        val retrofit = RetrofitClient.retrofit
        val roomService = retrofit.create(RoomService::class.java)
        viewModel.fetchRooms(roomService)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    fun onRoomUpdated(room: Room){
        viewModel.onNewRoomEstimated(room)
    }

    interface OnFragmentInteractionListener {
        fun onLogin(callback: LoginManager.SimpleCallback)
        fun onListFragmentInteraction(item: Question?)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
