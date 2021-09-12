package com.azharkova.kmm_concurrency_sample

import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.*
import platform.darwin.NSObject

class ResponseReader: NSObject(), NSURLSessionDataDelegateProtocol {
    var responseListener: ResponseListener? = null
    override fun URLSession(
        session: NSURLSession,
        dataTask: NSURLSessionDataTask,
        didReceiveData: NSData
    ) {
        responseListener?.receiveData(didReceiveData)
    }

    override fun URLSession(
        session: NSURLSession,
        task: NSURLSessionTask,
        didCompleteWithError: NSError?
    ) {
        val response = task.response as NSHTTPURLResponse
        responseListener?.completed(response.statusCode, didCompleteWithError as? Error)
    }
}