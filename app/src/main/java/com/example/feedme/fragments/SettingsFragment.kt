package com.example.feedme.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.feedme.R

class SettingsFragment : Fragment(){

    private var listener: OnFragmentInteractionListener? = null

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val layoutInflater = inflater.inflate(R.layout.settings_fragment, container, false)

        val questionBtn = layoutInflater.findViewById<Button>(R.id.button)
        questionBtn.setOnClickListener(View.OnClickListener {
            listener?.onFragmentInteraction()
        })

        return layoutInflater
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SettingsFragment.OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }

}
