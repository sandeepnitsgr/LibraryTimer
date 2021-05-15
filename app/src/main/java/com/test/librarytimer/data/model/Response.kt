package com.test.librarytimer.data.model

enum class Status {
    LOADING, SUCCESS, ERROR
}
class Response private constructor(val status: Status, val data: Any?, val error: Throwable?) {
    companion object {
        @JvmStatic
        internal fun loading(): Response {
            return Response(Status.LOADING, null, null)
        }

        @JvmStatic
        fun success(data: Any?): Response {
            return Response(Status.SUCCESS, data, null)
        }

        @JvmStatic
        fun error(error: Throwable): Response {
            return Response(Status.ERROR, null, error)
        }
    }
}