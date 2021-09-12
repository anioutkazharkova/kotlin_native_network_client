package com.azharkova.kmm_concurrency_sample.deferred

import com.azharkova.kmm_concurrency_sample.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import platform.Foundation.*
import platform.darwin.NSObject

class FlowResponseReader : NSObject(),
    NSURLSessionDataDelegateProtocol {
    private var chunksFlow = MutableStateFlow(ByteArray(0))
    private var rawResponse = CompletableDeferred<Response>()

    suspend fun awaitResponse(): Response {
        var chunks = ByteArray(0)

        chunksFlow.onEach {
            chunks += it
        }.launchIn(scope)
        val response = rawResponse.await()
        response.content = chunks.string()
        return response.share()
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
        if (!rawResponse.isCompleted) {
            rawResponse.complete(Response(code, "", error))
            clearChunks()
        } else {
            NSLog("already completed:")
        }
    }

    private fun updateChunks(data: NSData) {
        val bytes = data.toByteArray().share()
        chunksFlow.tryEmit(bytes)
    }

    private fun clearChunks() {
    }
}