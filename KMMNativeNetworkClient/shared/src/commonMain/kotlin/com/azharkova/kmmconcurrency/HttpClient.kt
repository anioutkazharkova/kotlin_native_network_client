package com.azharkova.kmmconcurrency

import com.azharkova.kmm_concurrency_sample.*


class HttpClientCommon {
    val httpClient: IHttpClient = HttpClient()

    fun request(request: Request, completion: (Response) -> Unit) {
        httpClient.request(request, completion)
    }

    fun request(url: String, method: String, headers: Map<Any?,String>, completion: (Response) -> Unit) {
        val r = Request(url,method = Method.valueOf(method),headers)
        httpClient.request(r,completion)
    }

    suspend fun request(request: Request): Response {
        return httpClient.request(request)
    }

    suspend fun request(url: String, method: String, headers: Map<Any?,String>):Response {
        val r = Request(url,method = Method.valueOf(method),headers)
       return request(r)
    }
}

