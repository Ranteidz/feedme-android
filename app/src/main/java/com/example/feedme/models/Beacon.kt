package com.example.feedme.models

class Beacon(val name: String, val building: String, val uuid: String) {
    private val SIGNAL_MAX = 100
    private val rssis = arrayOf(SIGNAL_MAX, SIGNAL_MAX, SIGNAL_MAX, SIGNAL_MAX, SIGNAL_MAX)
    var index = 0
    private val SIGNAL_AMOUNT = 5

    fun addRssi(signal: Int) {
        index = (index + 1) % SIGNAL_AMOUNT
        rssis[index] = signal
    }

    fun averageRssi(): Int {
        var sum = 0

        for (i in 0..SIGNAL_AMOUNT) {
            sum += rssis[i]
        }

        return sum / SIGNAL_AMOUNT
    }
}