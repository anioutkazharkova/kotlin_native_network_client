package com.azharkova.kmm_network

interface IHttpClient {
    fun request(request: Request, completion: (Response)->Unit)

    suspend fun  request(request: Request):Response
}

expect  class  HttpClient constructor() : IHttpClient {
    override fun request(request: Request, completion: (Response)->Unit)

    override suspend fun request(request: Request):Response
}