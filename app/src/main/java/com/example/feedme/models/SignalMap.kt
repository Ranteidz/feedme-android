package com.example.feedme.models

data class SignalMap (val beaconSignals: Array<BeaconSignal>, val room: String, val isActive: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignalMap

        if (!beaconSignals.contentEquals(other.beaconSignals)) return false
        if (room != other.room) return false
        if (isActive != other.isActive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = beaconSignals.contentHashCode()
        result = 31 * result + room.hashCode()
        result = 31 * result + isActive.hashCode()
        return result
    }
}