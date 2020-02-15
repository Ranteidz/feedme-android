package com.example.feedme.models
data class BeaconSignal (val beacon: String, val signals: Array<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeaconSignal

        if (beacon != other.beacon) return false
        if (!signals.contentEquals(other.signals)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = beacon.hashCode()
        result = 31 * result + signals.contentHashCode()
        return result
    }
}