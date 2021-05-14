package com.test.librarytimer.data.model

import com.google.gson.annotations.SerializedName


data class ScanResult(
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("location_details")
    val locationDetails: String,
    @SerializedName("price_per_min")
    val pricePerMinute: Float
)
