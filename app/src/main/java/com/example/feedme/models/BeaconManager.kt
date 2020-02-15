package com.example.feedme.models

import android.util.Log
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import java.util.concurrent.TimeUnit


class BeaconManager(private val proximityManager: ProximityManager) : IBeaconListener {
    private val TAG = "BeaconManager"

    init {
        proximityManager.setIBeaconListener(this)
    }

    fun connect() {
        proximityManager.configuration()
            .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(1))
        proximityManager.connect { proximityManager.startScanning() }
    }

    fun stopScanning() {
        proximityManager.stopScanning()
    }

    fun disconnect() {
        proximityManager.disconnect()
    }

    override fun onIBeaconLost(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {
        val beaconName = iBeacon.toString()
        Log.i(TAG, "Connection to $beaconName was lost")
    }

    override fun onIBeaconsUpdated(iBeacons: MutableList<IBeaconDevice>?, region: IBeaconRegion?) {
        if (iBeacons != null) {
            for (beacon in iBeacons) {
                val id = beacon.uniqueId
                val rssi = beacon.rssi.toString()
                Log.i(TAG, "$id: $rssi")

            }
        }
    }

    override fun onIBeaconDiscovered(iBeacon: IBeaconDevice?, region: IBeaconRegion?) {
        val beaconName = iBeacon.toString()
        Log.i(TAG, "IBeacon discovered: $beaconName")
    }
}