package com.example.feedme.models

import java.time.temporal.Temporal

class RssiValue (val value: Int, val time: Temporal){
    override fun toString(): String {
        return "RssiValue(value=$value, time=$time)"
    }
}
