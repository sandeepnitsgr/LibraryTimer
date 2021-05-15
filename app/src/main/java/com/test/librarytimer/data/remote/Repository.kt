package com.test.librarytimer.data.remote

import com.test.librarytimer.data.model.PostResponse
import com.test.librarytimer.data.model.RequestParam
import com.test.librarytimer.data.remote.retrofit.ApiService
import io.reactivex.Observable
import javax.inject.Inject

class Repository @Inject constructor(private val service: ApiService) {
    fun submitData(requestParam: RequestParam): Observable<PostResponse> {
        return service.submitData(requestParam)
    }

}