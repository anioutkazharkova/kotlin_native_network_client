package com.azharkova.kmm_concurrency_sample.deferred

import com.azharkova.kmm_concurrency_sample.*
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.*
import platform.darwin.NSObject

class DefferedResponseReader: NSObject(), NSURLSessionDataDelegateProtocol {
    private var chunks = ByteArray(0).atomic()
    private var rawResponse = CompletableDeferred<Response>()

    suspend fun awaitResponse(): Response {
        return rawResponse.await().share()
    }

    override fun URLSession(
        session: NSURLSession,
        dataTask: NSURLSessionDataTask,
        didReceiveData: NSData
    ) {
        this.updateChunks(didReceiveData)
    }

    override fun URLSession(
        session: NSURLSession,
        task: NSURLSessionTask,
        didCompleteWithError: NSError?
    ) {
        val response = task.response as NSHTTPURLResponse
        completed(response.statusCode,didCompleteWithError as? Error)
    }

    fun completed(code: Long, error: Error?) {
        val content = chunks.value.string()

        if (!rawResponse.isCompleted) {

            NSLog("completed: %s",content)
            rawResponse.complete(Response(code, content, error))
            clearChunks()
        } else {
            NSLog("already completed:")
        }
    }

    private fun updateChunks(data: NSData) {
        var newValue = ByteArray(0)
        newValue += chunks.value
        newValue += data.toByteArray()
        chunks.value = newValue.share()
    }

    private fun clearChunks() {
        chunks.value = ByteArray(0).share()
    }
}