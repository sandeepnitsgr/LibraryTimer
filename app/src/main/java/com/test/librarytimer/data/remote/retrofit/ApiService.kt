package com.test.librarytimer.data.remote.retrofit

import com.test.librarytimer.data.model.PostResponse
import com.test.librarytimer.data.model.RequestParam
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/submit-session")
    fun submitData(@Body request : RequestParam) : Observable<PostResponse>
}