package com.example.record_a_day.data

import com.google.gson.annotations.SerializedName


data class RecordItem (
    @SerializedName("TITLE")
    var title: String,
    @SerializedName("DATE")
    val date:String,
    @SerializedName("WEATHER")
    val weather:String
    )