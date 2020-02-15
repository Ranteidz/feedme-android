package com.example.feedme.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.feedme.models.SignalMapResponse
import com.example.feedme.services.SignalMapService
import com.example.feedme.services.SimpleCallback
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.configuration.GeneralConfigurator
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class BeaconViewModelTest {


    @Mock lateinit var proximityManager: ProximityManager
    @Mock lateinit var iBeaconDevice: IBeaconDevice
    @Mock lateinit var configurator: GeneralConfigurator
    @Mock lateinit var signalMapService: SignalMapService
    @Mock lateinit var signalMapServiceCallback: SimpleCallback<SignalMapResponse>

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    lateinit var proximityUUID: UUID

    lateinit var beaconViewModel: BeaconViewModel

    lateinit var now : LocalDateTime

    @Before fun setUp() {

        MockitoAnnotations.initMocks(this)
        proximityUUID = UUID.randomUUID()
        `when`(proximityManager.configuration()).thenReturn(configurator)
        `when`(iBeaconDevice.proximityUUID).thenReturn(proximityUUID)
        beaconViewModel = BeaconViewModel(proximityManager, signalMapService, signalMapServiceCallback )
        now = LocalDateTime.now()
    }

    @Test fun onIBeaconLostShouldMaintainBeaconRssi() {
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)
        beaconViewModel.onIBeaconLost(iBeaconDevice, null)

        assertThat(beaconViewModel.beacons.size).isEqualTo(1)

    }

    @Test fun onIBeaconsUpdated_addRssi() {
        val rssi = 10

        `when`(iBeaconDevice.rssi).thenReturn(5)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)
        `when`(iBeaconDevice.rssi).thenReturn(rssi)
        beaconViewModel.onIBeaconsUpdated(mutableListOf(iBeaconDevice), null)

        assertThat(beaconViewModel.beacons[proximityUUID]!!.rssiValues[0].value).isEqualTo(5)
    }

    @Test fun beaconDiscoveryShouldAddRssi() {
        val rssi = 10
        `when`(iBeaconDevice.rssi).thenReturn(rssi)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice,null)

        assertThat(beaconViewModel.beacons[proximityUUID]!!.rssiValues[0].value).isEqualTo(rssi)
    }

    @Test fun onIBeaconDiscovered() {
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)
        assertThat(beaconViewModel.beacons.size).isEqualTo(1)
    }

    @Test fun shouldMaintainRssiWhenAlreadyExistingBeaconWasAdded() {
        val iBeaconDevice2 = Mockito.spy(IBeaconDevice::class.java)
        `when`(iBeaconDevice2.rssi).thenReturn(20)
        `when`(iBeaconDevice2.proximityUUID).thenReturn(proximityUUID)
        `when`(iBeaconDevice.rssi).thenReturn(10)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice2, null)
        assertThat(beaconViewModel.beacons[proximityUUID]!!.averageRssi(now)).isEqualTo(15)
    }

    @Test fun shouldNotGiveDivideByZeroError() {
        `when`(iBeaconDevice.rssi).thenReturn(10)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)
        `when`(iBeaconDevice.rssi).thenReturn(-10)
        beaconViewModel.onIBeaconDiscovered(iBeaconDevice, null)

        assertThat(beaconViewModel.beacons[proximityUUID]!!.averageRssi(now)).isEqualTo(0)
    }

    @Test fun shouldEstimateAndSetRoom(){

        val server = MockWebServer()
        server.start()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val service = retrofit.create(SignalMapService::class.java)

        server.enqueue(MockResponse().setResponseCode(200).setBody(
            "{\"room\": {\"_id\": \"hej\"," +
                    "\"name\" : \"041\"," +
                    "\"building\": \"324\"}," +
                    "\"isActive\": \"true\"}"
        ))

        val latch = CountDownLatch(1)

        val signalMapServiceCallback = object: SimpleCallback<SignalMapResponse>{
            override fun onSuccess(body: SignalMapResponse) {
                latch.countDown()
            }

            override fun onError(statusCode: Int, message: String) {
                fail("Should not return error")
            }
        }

        val beaconModel = BeaconViewModel.BeaconModelFactory(proximityManager,
            service, signalMapServiceCallback).create(BeaconViewModel::class.java)

        val jwt = "hej"
        beaconModel.estimateRoom(jwt)
        latch.await(1, TimeUnit.SECONDS)


        assertThat(beaconModel.currentRoom.value).isEqualTo("041")


    }

}