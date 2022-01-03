package com.azharkova.kmm_network

import com.azharkova.kmm_network.deferred.HttpDefferedEngine
import kotlinx.coroutines.withContext
import platform.Foundation.NSLog

actual class HttpClient : IHttpClient {
    val httpEngine = HttpEngine()
    val httpDefferedEngine = HttpDefferedEngine()

    actual override fun request(request: Request, completion: (Response) -> Unit) {

        httpEngine.request(request) {
           completion(it)
        }
    }

    actual override suspend fun request(request: Request):Response {

        val response = withContext(ioDispatcher) {
            httpDefferedEngine.request(request)
        }
        return response
    }
}



