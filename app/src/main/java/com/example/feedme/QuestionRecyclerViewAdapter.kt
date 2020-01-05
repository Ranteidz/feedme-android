package com.example.feedme

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.feedme.models.Question
import kotlinx.android.synthetic.main.fragment_question.view.*

/**
 * [RecyclerView.Adapter] that can display a [Question] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class QuestionRecyclerViewAdapter() :
    RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder>() {
    val TAG = "QuestionRecyclerViewAdapter"
    private val mValues = ArrayList<Question>()
    private lateinit var interactionListener: QuestionInteractionListener

    fun setQuestionsList(questions: List<Question>?, listener: QuestionInteractionListener) {
        interactionListener = listener
        mValues.clear()
        if (questions != null) {
            mValues.addAll(questions)
        } else {
            Log.i(TAG, "Questions null")
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_question, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val answerOptions = ArrayList<String>()
        for (answerOption in item.answerOptions) {
            if (answerOption.value != null) {
                answerOptions.add(answerOption.value)
            }
        }
        val adapter = ArrayAdapter<String>(
            holder.mSpinner.context,
            android.R.layout.simple_spinner_item,
            answerOptions
        )
        holder.mSpinner.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        holder.mSendBtn.setOnClickListener {
            interactionListener.onSendClick(position, answerOptions.indexOf(holder.mSpinner.selectedItem.toString()))
        }

        /*holder.mYesOptionView.text = item.answerOptions[0].value
            holder.mSendBtn.text = item.answerOptions[1].value
            holder.mYesOptionView.setOnClickListener {
                interactionListener.onAnswerClick(
                    position,
                    0
                )
            }
            holder.mSendBtn.setOnClickListener {
                interactionListener.onAnswerClick(
                    position,
                    1
                )
            }
            holder.mSendBtn.visibility = View.VISIBLE
            holder.mSpinner.visibility = View.INVISIBLE
        }*/

        holder.mTitleView.text = item.value

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.title
        val mSendBtn: Button = mView.sendBtn
        val mSpinner: Spinner = mView.questionSpinner

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    interface QuestionInteractionListener {
        fun onSendClick(position: Int, answer: Int)
    }
}
