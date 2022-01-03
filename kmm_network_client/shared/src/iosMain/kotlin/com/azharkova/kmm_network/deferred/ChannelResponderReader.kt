package com.azharkova.kmm_network.deferred


import com.azharkova.kmm_network.*
import com.azharkova.kmm_network.share
import com.azharkova.kmm_network.string
import com.azharkova.kmm_network.toByteArray
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import platform.Foundation.*
import platform.darwin.NSObject

class ChannelResponderReader : NSObject(), NSURLSessionDataDelegateProtocol {
    private var chunks = Channel<ByteArray>(UNLIMITED)
    private var rawResponse = CompletableDeferred<Response>()

   suspend fun awaitResponse(): Response {
        var array = ByteArray(0)
        var response = rawResponse.await()
        chunks.consumeEach {
            array += it
        }

       response.content = array.string()
        NSLog("ready")
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
            NSLog("completed:")

            chunks.close()
            rawResponse.complete(Response(code, "", error))
        } else {
            chunks.cancel()
            NSLog("already completed:")
        }
    }

    private fun updateChunks(data: NSData) {
        val bytes = data.toByteArray()
        scope.launch {
            chunks.send(bytes)
        }
    }

    private fun clearChunks() {
        chunks.close()
    }
}