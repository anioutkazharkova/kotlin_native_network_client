package com.azharkova.kmm_concurrency_sample

actual class HttpClient : IHttpClient {
    actual override fun request(request: Request, completion: (Response)->Unit) {
        TODO("Not yet implemented")
    }

    actual override suspend fun request(request: Request): Response {
        TODO("Not yet implemented")
    }


}