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
import android.util.Log
import android.widget.Toast
import com.example.feedme.fragments.*
import com.example.feedme.services.RetrofitClient


class MainActivity : AppCompatActivity(), SettingsFragment.OnFragmentInteractionListener,
    FeedbackFragment.OnFragmentInteractionListener {
    override fun onLogin(callback: LoginManager.SimpleCallback) {
        login(true, callback)
    }

    override fun onListFragmentInteraction(item: Question?) {
        Toast.makeText(this, "Item clicked :) ", Toast.LENGTH_LONG).show()
    }

    override fun onFragmentInteraction(questions: ArrayList<Question>) {
        val intent = Intent(this, QuestionnaireActivity::class.java)
        intent.putExtra("fragcount", 3)
        intent.putExtra("questions", questions)

        startActivity(intent)
    }

    val TAG = "MainActivity"
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "com.feedme.prefs"
    private val fragment1: Fragment = FeedbackFragment.newInstance()
    private val fragment2: Fragment = FeedbackFragment.newInstance()
    private val fragment3: Fragment = SettingsFragment.newInstance()
    private val fm: FragmentManager = supportFragmentManager
    var active = fragment1

    private lateinit var retrofit: Retrofit
    private lateinit var loginManager: LoginManager


    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

        loginManager = LoginManager(getSharedPreferences(PREF_NAME, PRIVATE_MODE))

        login(true, object : LoginManager.SimpleCallback {
            override fun onSuccess() {

                when (val jwt = loginManager.getJwtToken()) {
                    is Success -> addFragments(jwt.value)
                    is Failure -> showError(jwt.error)
                }
            }
            override fun onFailure(errorCode: Int) {
            }
        })
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun addFragments(jwt: String){
        val args = Bundle()
        args.putString("jwt", jwt)
        Log.i(TAG, "JWT: $jwt")
        fragment1.arguments = args
        fragment2.arguments = args
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit()
    }

    private fun login(forceCall: Boolean, callback: LoginManager.SimpleCallback) {
        loginManager.anonymousLogin(forceCall, callback)
    }

    private fun showError(e: Throwable) {
        println(e)
    }

    private fun getQuestions(jwt: String) {
        Toast.makeText(this, "HEJJ", Toast.LENGTH_LONG).show()
        val roomId = getRooms()
        val questionService = retrofit.create(QuestionService::class.java)
        val call = questionService.getQuestions(jwt, roomId, true)
        call.enqueue(object : Callback<ArrayList<Question>?> {
            override fun onFailure(call: Call<ArrayList<Question>?>, t: Throwable) {
                Log.i(TAG, "HEJJJJJ")
                Log.i(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Question>?>,
                response: Response<ArrayList<Question>?>
            ) {
                Log.i(TAG, "YOOOO")
                val questions = response.body()
            }
        })
    }

    private fun getRooms(): String {
        return "5d8f18807f67046100eb863f"
    }


}
