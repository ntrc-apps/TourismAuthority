package com.example.navigationcomponent


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourDetails(
        val imageSrc : Int,
        val imageTitle: String,
        val imageDesc: String,
) : Parcelable