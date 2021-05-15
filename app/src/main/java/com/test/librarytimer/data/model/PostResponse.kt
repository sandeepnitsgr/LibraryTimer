package com.test.librarytimer.data.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("success")
    private val success: Boolean
)
