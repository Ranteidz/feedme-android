package com.example.feedme.fragments

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class FeedbackViewModelFactory(private val jwt: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FeedbackViewModel(jwt) as T
    }
}