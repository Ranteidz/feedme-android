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
    private lateinit var questionsAdapter : QuestionRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val jwt =
            arguments?.getString("jwt") ?: throw RuntimeException("$context must send jwt token")
        binding = FeedbackFragmentBinding.inflate(inflater, container, false)

        questionsAdapter = QuestionRecyclerViewAdapter()
        binding.questionsListView.layoutManager = LinearLayoutManager(context)
        binding.questionsListView.adapter = questionsAdapter

        binding.refreshBtn.setOnClickListener {
            viewModel.refresh(jwt)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FeedbackViewModel::class.java)
        viewModel.questions.observe(this, Observer { questionsAdapter.setQuestionsList(it, viewModel) })
        // TODO: Use the ViewModel
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
