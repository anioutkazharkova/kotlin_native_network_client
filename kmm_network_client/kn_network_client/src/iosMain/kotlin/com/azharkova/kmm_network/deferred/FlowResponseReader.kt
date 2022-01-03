package com.azharkova.kmm_network.deferred

import com.azharkova.kmm_network.*
import com.azharkova.kmm_network.atomic
import com.azharkova.kmm_network.share
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*
import platform.Foundation.*
import platform.darwin.NSObject

class FlowResponseReader : NSObject(),NSURLSessionDataDelegateProtocol {
    private var chunksFlow = MutableStateFlow(ByteArray(0))
    private var rawResponse = CompletableDeferred<Response>()

   suspend fun awaitResponse(): Response {
        var chunks = ByteArray(0).atomic()

        chunksFlow.onEach {
            var newValue = ByteArray(0)
            newValue += chunks.value
            newValue += it
            chunks.value = newValue.share()
        }.launchIn(scope)
        val response = rawResponse.await()
        response.content = chunks.value.string()
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