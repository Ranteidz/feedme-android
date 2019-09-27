package com.example.feedme

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.feedme.models.LoginManager
import com.example.feedme.models.LoginManager.JwtResult.*
import com.example.feedme.models.Question
import com.example.feedme.services.QuestionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.Toast
import com.example.feedme.fragments.*
import com.example.feedme.fragments.dummy.DummyContent


class MainActivity : AppCompatActivity(), SettingsFragment.OnFragmentInteractionListener  {
    override fun onFragmentInteraction() {
        startActivity(Intent(this, QuestionnaireActivity::class.java))
    }


    val TAG = "MainActivity"
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "com.feedme.prefs"
    private val BASE_REMOTE_URL = "http://feedme.compute.dtu.dk/api/"
    private val BASE_LOCAL_URL = "http://10.0.2.2/api/"
    private val fragment1: Fragment = FeedbackFragment()
    private val fragment2: Fragment = FeedbackFragment()
    private val fragment3: Fragment = SettingsFragment()
    private val fm: FragmentManager = supportFragmentManager
    var active = fragment1

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_LOCAL_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                fm.beginTransaction().hide(active).show(fragment1).commit()
                active = fragment1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                fm.beginTransaction().hide(active).show(fragment2).commit()
                active = fragment2
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                fm.beginTransaction().hide(active).show(fragment3).commit()
                active = fragment3
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit()


        val loginManager = LoginManager(getSharedPreferences(PREF_NAME, PRIVATE_MODE), retrofit)
        loginManager.anonymousLogin(true, object: LoginManager.SimpleCallback{
            override fun onSuccess() {
            }

            override fun onFailure(errorCode: Int) {
            }

        })

        when (val jwt = loginManager.getJwtToken()){
            is Success -> getQuestions(jwt.value)
            is Failure -> showError(jwt.error)
        }

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun showError(e: Throwable) {
        println(e)
    }

    private fun getQuestions(jwt: String) {

        val roomId = getRooms()
        val questionService = retrofit.create(QuestionService::class.java)
        val call = questionService.getQuestions(jwt, roomId)
        call.enqueue(object : Callback<List<Question>?> {
            override fun onFailure(call: Call<List<Question>?>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<List<Question>?>,
                response: Response<List<Question>?>
            ) {
            }
        })
    }

    private fun getRooms() : String {
        return "5cdb11ab5b29b066ecb15144"
    }


}
