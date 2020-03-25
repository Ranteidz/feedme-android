package com.example.feedme.ui.questionnaire

import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.feedme.R

class QuestionnaireFragment : Fragment() {
    private val TAG = "QuestionnaireFragment"

    companion object {
        fun newInstance() = QuestionnaireFragment()
    }

    private lateinit var viewModel: QuestionnaireViewModel

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val question = arguments?.getString("question")
        val answerOptions = arguments?.getStringArray("answerOptions")

        val view = inflater.inflate(R.layout.fragment_questionnaire, container, false)
        val txt = view.findViewById<TextView>(R.id.message)

//        btn.setOnClickListener {
//            listener?.onFragmentInteraction()
//        }


        val bottomBtn = Button(context)
        val constraintSet = ConstraintSet()

        bottomBtn.text = getString(android.R.string.ok)


        bottomBtn.id = R.id.new_button_id

        val cl = view.findViewById<ConstraintLayout>(R.id.mainactivity2)
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        lp.marginEnd = 50
        lp.marginStart = 50
        bottomBtn.background = context?.let { ContextCompat.getDrawable(it, R.color.primaryColor) }
        cl.addView(bottomBtn, lp)

        constraintSet.clone(cl)

        constraintSet.connect(
            bottomBtn.id,
            ConstraintSet.BOTTOM,
            cl.id,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.applyTo(cl)


        if (answerOptions != null) {
            for (i in answerOptions.indices) {
                val newBtn = Button(context)

                newBtn.text = "Ok"

                newBtn.id = R.id.new_button_id + i + 1

                newBtn.background =
                    context?.let { ContextCompat.getDrawable(it, R.color.primaryColor) }

                cl.addView(newBtn, lp)
                constraintSet.clone(cl)

                constraintSet.connect(
                    newBtn.id,
                    ConstraintSet.BOTTOM,
                    newBtn.id - 1,
                    ConstraintSet.TOP,
                    0
                )
                constraintSet.applyTo(cl)
            }
        } else {

        }

        txt.text = question

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QuestionnaireViewModel::class.java)
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }

}
