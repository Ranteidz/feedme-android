package com.example.feedme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.feedme.fragments.FeedbackFragment
import com.example.feedme.fragments.SettingsFragment
import com.example.feedme.models.LoginManager
import com.example.feedme.models.LoginManager.JwtResult.Failure
import com.example.feedme.models.LoginManager.JwtResult.Success
import com.example.feedme.models.Question
import com.example.feedme.models.SignalMapResponse
import com.example.feedme.services.RetrofitClient
import com.example.feedme.services.SignalMapService
import com.example.feedme.services.SimpleCallback
import com.example.feedme.viewmodels.BeaconViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import java.util.concurrent.ScheduledExecutorService

const val PERMISSION_COARSE_LOCATION = 0

class MainActivity : AppCompatActivity(), SettingsFragment.OnFragmentInteractionListener,
    FeedbackFragment.OnFragmentInteractionListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        const val TAG = "MainActivity"
        const val PRIVATE_MODE = 0
        const val PREF_NAME = "com.feedme.prefs"
    }

    private val fragment1: Fragment = FeedbackFragment.newInstance()
    private val fragment2: Fragment = SettingsFragment.newInstance()
    private val fm: FragmentManager = supportFragmentManager
    var active = fragment1
    private lateinit var loginManager: LoginManager
    private lateinit var beaconViewModel: BeaconViewModel
    private lateinit var layout: View
    private lateinit var service : ScheduledExecutorService
    private lateinit var jwt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        loginManager = LoginManager(getSharedPreferences(PREF_NAME, PRIVATE_MODE))

        val proximityManager = ProximityManagerFactory.create(this)

        val signalMapService = RetrofitClient.retrofit.create(SignalMapService::class.java)

        val signalMapCallback = object: SimpleCallback<SignalMapResponse>{
            override fun onSuccess(body: SignalMapResponse) {

//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(statusCode: Int, message: String) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        beaconViewModel = ViewModelProvider(this,
            BeaconViewModel.BeaconModelFactory(proximityManager, signalMapService, signalMapCallback ))
        .get(BeaconViewModel::class.java)

        beaconViewModel.currentRoom.observe(this, Observer { room ->
            if (active is FeedbackFragment) {
                (active as FeedbackFragment).onRoomUpdated(room)
            }
            supportActionBar?.title = room.name

        })


        layout = findViewById(R.id.container)

        login(true, object : LoginManager.SimpleCallback {
            override fun onSuccess() {

                when (val jsonWebToken = loginManager.getJwtToken()) {
                    is Success -> {
                        jwt = jsonWebToken.value
                        beaconViewModel.startScanning(jwt)
                        addFragments(jsonWebToken.value)
                    }
                    is Failure -> showError(jsonWebToken.error)
                }
            }
            override fun onFailure(errorCode: Int) {
            }
        })
//        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = "HEJ"
                Log.d(TAG, token)
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_COARSE_LOCATION) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                val snackbar = Snackbar.make(layout, "granted", Snackbar.LENGTH_SHORT).show()
//                if (actionMessage != null) {
//                    snackbar.setAction("granted") {
////                        action(this)
//                    }.show()
//                }
//                layout.showSnackbar(R.string.camera_permission_granted, Snackbar.LENGTH_SHORT)
//                startCamera()
            } else {

                // Permission request was denied.
                val snackbar = Snackbar.make(layout, "denied", Snackbar.LENGTH_SHORT).show()
//                snackbar.setAction("denied") {
//                    //                        action(this)
//                }.show()
            }
        }
    }



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
              /*  R.id.navigation_notifications -> {
                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                    return@OnNavigationItemSelectedListener true
                }*/
            }
            false
        }

    private fun addFragments(jwt: String) {
        val args = Bundle()
        args.putString("jwt", jwt)
        Log.i(TAG, "JWT: $jwt")
        fragment1.arguments = args
        fragment2.arguments = args
//        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit()
    }

    private fun login(forceCall: Boolean, callback: LoginManager.SimpleCallback) {
        loginManager.anonymousLogin(forceCall, callback)
    }

    private fun showError(e: Throwable) {
        println(e)
    }

    override fun onStart() {
        super.onStart()

        // Making sure the necessary permission is acquired and that login was successful,
        // thus obtaining a json web token.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ::jwt.isInitialized) {
            beaconViewModel.startScanning(jwt)
        } else {
            requestLocationPermission()
        }
    }

    override fun onStop() {
        beaconViewModel.stopScanning()
        super.onStop()
    }

    override fun onDestroy() {
        beaconViewModel.onDestroy()
        super.onDestroy()
    }

    private fun requestLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_COARSE_LOCATION)
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_COARSE_LOCATION)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
}
