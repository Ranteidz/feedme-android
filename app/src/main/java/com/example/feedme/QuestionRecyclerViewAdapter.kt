package com.example.feedme

import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feedme.models.Question
import kotlinx.android.synthetic.main.fragment_question.view.*

/**
 * [RecyclerView.Adapter] that can display a [Question] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class QuestionRecyclerViewAdapter() :
    RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder>() {
    private val mQuestions = ArrayList<Question>()
    private lateinit var interactionListener: QuestionInteractionListener

    fun setQuestionsList(questions: List<Question>?, listener: QuestionInteractionListener) {
        interactionListener = listener
        mQuestions.clear()
        if (questions != null) {
            mQuestions.addAll(questions)
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
        val question = mQuestions[position]
        val context = holder.mButtonLayout.context

        if (holder.mButtonLayout.childCount != question.answerOptions.size) {
            holder.mButtonLayout.removeAllViews()
            for (answer in question.answerOptions){
                val btnStyle = android.R.style.Widget_Material_Button
                val button = Button(ContextThemeWrapper(context, btnStyle), null, btnStyle)
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val r = context.resources
                val sideMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, r.displayMetrics).toInt()
                val topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, r.displayMetrics).toInt()

                layoutParams.setMargins(sideMargin , topMargin, sideMargin , 0)
                button.layoutParams = layoutParams

                button.text = answer.value
                button.setOnClickListener { interactionListener.onAnswer(question._id, answer._id) }
                holder.mButtonLayout.addView(button)

            }
        }


        holder.mTitleView.text = question.value

        with(holder.mView) {
            tag = "item"
        }
    }

    override fun getItemCount(): Int = mQuestions.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.title
        val mButtonLayout: LinearLayout = mView.buttonLayout
//        val mSendBtn: Button = mView.sendBtn
//        val mSpinner: Spinner = mView.questionSpinner

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    interface QuestionInteractionListener {
        fun onAnswer(questionId: String, answerId: String)
    }

    companion object {
        const val TAG = "QuestionRecyclerViewAdapter"
    }
}
