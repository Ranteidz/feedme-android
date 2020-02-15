package com.example.feedme.models

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

const val MAX_RSSI = 100

class BeaconTest {


    lateinit var beacon : Beacon
    lateinit var now : LocalDateTime

    @Before fun setUp() {
        beacon = Beacon()
        now = LocalDateTime.now()
    }

    @Test fun addRssi() {
        val rssi = RssiValue(75, now)
        beacon.addRssi(rssi)
        assertThat(beacon.rssiValues.size).isEqualTo(1)
    }

    @Test fun shouldCalcAverage() {
        beacon.addRssi(RssiValue(100, now))
        beacon.addRssi(RssiValue(50, now))
        assertThat(beacon.averageRssi(now)).isEqualTo(75)
    }

    @Test fun shouldRemoveOldBeforeCalcAverage() {
        beacon.addRssi(RssiValue(100, now))
        beacon.addRssi(RssiValue(50, LocalDateTime.MAX))
        beacon.addRssi(RssiValue(25, LocalDateTime.MIN))
        assertThat(beacon.rssiValues.size).isEqualTo(3)

        val average = beacon.averageRssi(now)
        assertThat(average).isEqualTo(100)
        assertThat(beacon.rssiValues.size).isEqualTo(1)
    }


    @Test fun shouldReturn100WhenEmptyRssiList(){
        val average = beacon.averageRssi(now)
        assertThat(average).isEqualTo(MAX_RSSI)
    }

    @Test fun shouldDeleteWhenAddingIfSizeAbove10(){
        beacon.addRssi(RssiValue(100, now))
        for(i in 0..8){
            beacon.addRssi(RssiValue(100, LocalDateTime.MIN))
        }

        assertThat(beacon.rssiValues.size == 10)

        beacon.addRssi(RssiValue(100,now))
        assertThat(beacon.rssiValues.size).isEqualTo(2)
    }

}