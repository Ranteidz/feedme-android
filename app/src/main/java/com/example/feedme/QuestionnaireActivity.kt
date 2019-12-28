package com.example.feedme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.feedme.models.Question
import com.example.feedme.ui.questionnaire.QuestionnaireFragment

class QuestionnaireActivity : AppCompatActivity(),
    QuestionnaireFragment.OnFragmentInteractionListener {
    private val fragments = mutableListOf<Fragment>()
    private var fragIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)


        val questions : ArrayList<Question> = intent.getSerializableExtra("questions") as ArrayList<Question>

        for (i in 0 until questions.size) {
            val args = Bundle()
            args.putInt("number", i)
            args.putString("question",questions[i].value)

            args.putSerializable("answerOptions", questions[i].answerOptions)
            val frag = QuestionnaireFragment.newInstance()
            frag.arguments = args
            fragments.add(frag)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragments[fragIndex])
                .commitNow()
        }
    }

    override fun onFragmentInteraction() {


        fragIndex = (fragIndex + 1) % fragments.size
        val frag = fragments[fragIndex]
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.slide_out_right)
        transaction
            .replace(R.id.container, frag)
            .commitNow()

    }

}
