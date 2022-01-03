package com.azharkova.kmm_network.service

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonDecoder
import kotlin.native.concurrent.ThreadLocal

class JsonDecoder{
    @ThreadLocal
    companion object {
        val instance = JsonDecoder()
        val jsonDecoder = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    }

    inline fun<reified T> decode(json:String):ContentResponse<T> {
        val contentResponse = ContentResponse<T>()
        contentResponse.content = jsonDecoder.decodeFromString(json)
        return  contentResponse
    }
}

class ContentResponse<T> constructor(){
    var content: T? = null
    var errorResponse: ErrorResponse? = null
}

enum class ErrorType {
    Network, Auth, Tech, Other, NotFound , BadAnswer
}

class ErrorResponse {
    var code: Int = 0
    var errorType: String = ""
    var message: String = ""

    var type: ErrorType = ErrorType.Other

    constructor(errorType: ErrorType) {
        this.type = errorType
    }

    constructor(message: String) {
        this.message = message
    }
}