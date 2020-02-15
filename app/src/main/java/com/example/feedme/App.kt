package com.example.feedme

import android.app.Application
import com.kontakt.sdk.android.common.KontaktSDK

class App : Application() {
    @Override
    override fun onCreate() {
        super.onCreate()
        KontaktSDK.initialize(this);
    }

}