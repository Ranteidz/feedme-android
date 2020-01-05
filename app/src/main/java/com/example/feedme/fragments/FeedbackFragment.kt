package com.example.feedme.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.feedme.QuestionRecyclerViewAdapter
import com.example.feedme.databinding.FeedbackFragmentBinding
import com.example.feedme.models.LoginManager
import com.example.feedme.models.Question

class FeedbackFragment : Fragment() {
    companion object {
        fun newInstance() = FeedbackFragment()
    }

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewModel: FeedbackViewModel
    private lateinit var binding: FeedbackFragmentBinding
    private lateinit var questionsAdapter: QuestionRecyclerViewAdapter
    private lateinit var jwt: String
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        jwt =
            arguments?.getString("jwt") ?: throw RuntimeException("$context must send jwt token")
        binding = FeedbackFragmentBinding.inflate(inflater, container, false)

        questionsAdapter = QuestionRecyclerViewAdapter()
        binding.questionsListView.layoutManager = LinearLayoutManager(context)
        binding.questionsListView.adapter = questionsAdapter
        spinner = binding.spinner

        binding.refreshBtn.setOnClickListener {
            viewModel.refresh()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, FeedbackViewModelFactory(jwt))
            .get(FeedbackViewModel::class.java)
        viewModel.liveQuestions.observe(
            this,
            Observer {
                questionsAdapter.setQuestionsList(it, viewModel)
            })
        val adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_item,
            viewModel.roomNames
        )

        spinner.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewModel.liveRooms.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(context, "ØV ", Toast.LENGTH_LONG).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.selectedRoom = position
                }

            }
        viewModel.fetchRooms()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
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
