package com.azharkova.kmm_concurrency_sample

import platform.Foundation.NSData


interface ResponseListener {
    fun completed(code: Long, error: Error?)

    fun receiveData(data: NSData)
}