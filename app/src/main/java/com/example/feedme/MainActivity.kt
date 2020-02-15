package com.example.feedme

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.*
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
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.example.feedme.fragments.*
import com.example.feedme.models.BeaconManager
import com.example.feedme.services.RetrofitClient
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener
import com.kontakt.sdk.android.common.KontaktSDK
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.kontakt.sdk.android.common.profile.IEddystoneDevice
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace


class MainActivity : AppCompatActivity(), SettingsFragment.OnFragmentInteractionListener,
    FeedbackFragment.OnFragmentInteractionListener {

    private lateinit var beaconManager: BeaconManager
    val TAG = "MainActivity"
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "com.feedme.prefs"
    private val fragment1: Fragment = FeedbackFragment.newInstance()
    private val fragment2: Fragment = FeedbackFragment.newInstance()
    private val fragment3: Fragment = SettingsFragment.newInstance()
    private val fm: FragmentManager = supportFragmentManager
    var active = fragment1

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

        // Initializing manager for beacon communication
        KontaktSDK.initialize(this)
        val proximityManager = ProximityManagerFactory.create(this)
        beaconManager = BeaconManager(proximityManager)
    }

    private fun addFragments(jwt: String) {
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

    override fun onStart() {
        super.onStart()

        val checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermissionResult) {
            beaconManager.connect()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //we should show some explanation for user here
//                TODO: Impement dialog box for asking user again. Following line should be deleted
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                FireMissilesDialogFragment().show(supportFragmentManager, "hej")
                showExplanationDialog()
            } else {
                //request permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            }
        }
    }

    class FireMissilesDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Are you sure you don't want your location to be estimated? B-) ")
                    .setPositiveButton("No"
                    ) { _, _ ->
                        //                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                    }
                    .setNegativeButton("Yes, I'm not unsure that it's not a good idea"
                    ) { _, _ ->
                        // User cancelled the dialog
                    }
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (100 == requestCode) {
                //same request code as was in request permission
                beaconManager.connect()
                Log.i(TAG,  "CALLED2")
            }

        } else {
            showExplanationDialog()
            //not granted permission
            //show some explanation dialog that some features will not work
        }

    }

    private fun showExplanationDialog() {
        Log.e(TAG, "SOME FEATURES WILL NOT WORK :( ")
    }

    override fun onStop() {
        beaconManager.stopScanning()
        super.onStop()
    }

    override fun onDestroy() {
        beaconManager.disconnect()
        super.onDestroy()
    }
}
