package com.example.record_a_day.data

import com.google.gson.annotations.SerializedName


data class TaskItem (
    @SerializedName("CHECK")
    var check: Boolean,
    @SerializedName("CONTENT")
    var content: String
    )