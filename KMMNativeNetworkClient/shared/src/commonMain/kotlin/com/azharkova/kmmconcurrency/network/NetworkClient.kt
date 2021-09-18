package com.azharkova.kmmconcurrency.network

import com.azharkova.kmm_concurrency_sample.HttpClient
import com.azharkova.kmm_concurrency_sample.Method
import com.azharkova.kmm_concurrency_sample.Request
import com.azharkova.kmm_concurrency_sample.Response

class NetworkClient {
    val httpClient = HttpClient()

    fun doRequest(url: String, method: String, headers: Map<Any?,String>, completion: (Response)->Unit) {
        val request = Request(url, Method.valueOf(method), headers)
        httpClient.request(request, completion)
    }
}