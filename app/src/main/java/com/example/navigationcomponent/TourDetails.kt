package com.example.navigationcomponent


import android.os.Parcelable


data class TourDetails(
        val imageSrc : Int,
        val imageTitle: String,
        val imageDesc: String,
) : Parcelable