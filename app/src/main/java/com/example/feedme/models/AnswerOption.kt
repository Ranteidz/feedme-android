package com.example.feedme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AnswerOption(
    @SerializedName("value") val value: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnswerOption> {
        override fun createFromParcel(parcel: Parcel): AnswerOption {
            return AnswerOption(parcel)
        }

        override fun newArray(size: Int): Array<AnswerOption?> {
            return arrayOfNulls(size)
        }
    }
}