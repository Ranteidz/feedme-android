package com.example.feedme.models

import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T: Any>(initValue: T): MutableLiveData<T>() {

    init {
        value = initValue
    }

    override fun getValue(): T = super.getValue() as T
    override fun setValue(value: T) = super.setValue(value)
    override fun postValue(value: T) = super.postValue(value)
}