package com.azharkova.kmm_network

actual class HttpClient  actual constructor() : IHttpClient {
    private val engine: HttpEngine by lazy {
        HttpEngine()
    }
    actual override fun request(request: Request, completion: (Response)->Unit) {
        TODO("Not yet implemented")
    }

    actual override suspend fun request(request: Request): Response {
       return engine.request(request)
    }


}