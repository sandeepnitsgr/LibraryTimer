package com.test.librarytimer.data.model

import com.google.gson.annotations.SerializedName

data class RequestParam(
    @SerializedName("location_id")
    private val locationId: String,
    @SerializedName("time_spent")
    private val timeSpent: Int,
    @SerializedName("end_time")
    private val endTime: Long
)
