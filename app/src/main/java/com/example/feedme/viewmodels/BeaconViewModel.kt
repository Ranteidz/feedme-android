package com.example.feedme.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feedme.models.*
import com.example.feedme.services.SignalMapService
import com.example.feedme.services.SimpleCallback
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BeaconViewModel(
    private val proximityManager: ProximityManager,
    private val signalMapService: SignalMapService,
    private val signalMapServiceCallback: SimpleCallback<SignalMapResponse>
) : ViewModel(), IBeaconListener {
    val currentRoom = MutableLiveData<Room>()

    private lateinit var service: ScheduledExecutorService

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val beacons = HashMap<UUID, Beacon>()

    init {
        proximityManager.setIBeaconListener(this)
        proximityManager.configuration().deviceUpdateCallbackInterval(500)
    }

    fun startScanning(jwt: String) {
        if (proximityManager.isConnected)
            proximityManager.startScanning()
        else
            proximityManager.connect { proximityManager.startScanning() }

        service = Executors.newScheduledThreadPool(2)
        service.scheduleAtFixedRate({ estimateRoom(jwt) }, 3, 3, TimeUnit.SECONDS)
    }

    fun stopScanning() {
        proximityManager.stopScanning()
        service.shutdown()
    }

    fun onDestroy() {
        proximityManager.disconnect()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun estimateRoom(jwt: String) {
        val beaconMap = beacons.filter { it.value.hasRssiValues() }
//        if (beaconMap.isEmpty()) return

        beacons.clear()
        beaconMap.forEach{ (key,value) -> beacons[key] = value}

        val beaconsToBeSent = beaconMap.map {(key, value) ->
            val rssi = value.averageRssi(LocalDateTime.now())
            BeaconRequest(key.toString(), arrayListOf(rssi))
        }

        val signalMap = SignalMapRequest(beaconsToBeSent)

        signalMapService.estimateRoom(jwt, signalMap).enqueue(object : Callback<SignalMapResponse> {
            override fun onFailure(call: Call<SignalMapResponse>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(
                call: Call<SignalMapResponse>,
                response: Response<SignalMapResponse>
            ) {
                val room = Room("5e37d7eb32e52f707d6f6431", "Espresso House", "Copenhagen")
                currentRoom.postValue(room)
                signalMapServiceCallback.onSuccess(SignalMapResponse(ArrayList(), room, true))
                /*val body = response.body()
                val statusCode = response.code()
                val message = response.message()
                if (statusCode == 200 && body != null) {
                    if (currentRoom.value == null || currentRoom.value!!._id != body.room._id)
                        currentRoom.postValue(body.room)

                    signalMapServiceCallback.onSuccess(body)
                } else {
                    signalMapServiceCallback.onError(statusCode, message)
                }*/
            }
        })
    }

    override fun onIBeaconLost(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {

    }

    override fun onIBeaconsUpdated(iBeacons: MutableList<IBeaconDevice>?, region: IBeaconRegion?) {
        if (iBeacons == null) return

        val now = LocalDateTime.now()

        for (iBeacon in iBeacons) {
            val rssiValue = RssiValue(iBeacon.rssi, now)

            var beacon = beacons[iBeacon.proximityUUID]
            if (beacon == null) {
                beacon = Beacon()
                beacons[iBeacon.proximityUUID] = beacon
            }

            beacon.addRssi(rssiValue)
        }
    }

    override fun onIBeaconDiscovered(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {

        iBeacon?.let {
            var beacon = beacons[it.proximityUUID]
            val rssiValue = RssiValue(it.rssi, LocalDateTime.now())

            if (beacon == null) {
                beacon = Beacon()
                beacons[it.proximityUUID] = beacon
            }

            beacon.addRssi(rssiValue)
        }
    }

    class BeaconModelFactory(
        private val proximityManager: ProximityManager,
        private val signalMapService: SignalMapService,
        private val signalMapServiceCallback: SimpleCallback<SignalMapResponse>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BeaconViewModel(proximityManager, signalMapService, signalMapServiceCallback) as T
        }
    }

    private companion object {
        const val TAG = "BeaconModel"
    }
}