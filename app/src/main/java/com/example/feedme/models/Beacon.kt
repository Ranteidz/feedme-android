package com.example.feedme.models

import androidx.annotation.VisibleForTesting
import java.time.Duration
import java.time.LocalDateTime



class Beacon {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val rssiValues =  ArrayList<RssiValue>()

    fun addRssi(rssi : RssiValue) {
        rssiValues.add(rssi)

        if (rssiValues.size > MAX_RSSI_AMOUNT){
            rssiValues.removeIf{
                Duration.between(LocalDateTime.now(), it.time).abs().seconds > RSSI_EXPIRATION
            }
        }
    }

    fun averageRssi(now: LocalDateTime): Int {
        rssiValues.removeIf{
            Duration.between(now, it.time).abs().seconds > RSSI_EXPIRATION
        }

        if (rssiValues.size == 0) return MIN_RSSI_VALUE

        var average = 0

        for (rssi in rssiValues) {
            average += rssi.value
        }

        return average/rssiValues.size
    }

    fun hasRssiValues() : Boolean = rssiValues.size > 0

    private companion object {
        const val TAG = "Beacon"
        const val MIN_RSSI_VALUE = -100
        const val RSSI_EXPIRATION = 5
        const val MAX_RSSI_AMOUNT = 10
    }
}
