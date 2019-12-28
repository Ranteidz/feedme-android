package com.example.feedme

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.feedme.models.Question
import kotlinx.android.synthetic.main.fragment_question.view.*

/**
 * [RecyclerView.Adapter] that can display a [Question] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
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
        holder.mTitleView.text = item.value
        holder.mYesOptionView.setOnClickListener { interactionListener.onYesClick(position) }
        holder.mNoOptionView.setOnClickListener { interactionListener.onNoClick(position) }

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.title
        val mYesOptionView: Button = mView.yes
        val mNoOptionView: Button = mView.no

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    interface QuestionInteractionListener {
        fun onYesClick(position: Int)
        fun onNoClick(position: Int)
    }
}
