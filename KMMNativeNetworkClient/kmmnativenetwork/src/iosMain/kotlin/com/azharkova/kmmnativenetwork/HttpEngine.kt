package com.azharkova.kmm_concurrency_sample

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import platform.Foundation.*

class HttpEngine : ResponseListener {
    private var completion: ((Response) -> Unit)? = null
    private val chunks = ByteArray(0).atomic()

    fun request(request: Request, completion: (Response) -> Unit) {
        this.completion = completion
        val responseReader = ResponseReader().apply { this.responseListener = this@HttpEngine }
        val urlSession =
            NSURLSession.sessionWithConfiguration(
                NSURLSessionConfiguration.defaultSessionConfiguration, responseReader.share(),
                delegateQueue = NSOperationQueue.currentQueue()
            )

        val urlRequest =
            NSMutableURLRequest(NSURL.URLWithString(request.url)!!).apply {
                setAllHTTPHeaderFields(request.headers)
                setHTTPMethod(request.method.value)
                setCachePolicy(NSURLRequestReloadIgnoringCacheData)

            }

        fun doRequest() {
            val task = urlSession.share().dataTaskWithRequest(urlRequest)
            task?.resume()
        }

        background{
            doRequest()
        }
    }

    override fun completed(code: Long, error: Error?) {
        main {
            val content = chunks.value.string()
           completion?.invoke(Response(code, content, error))
            //clear()
        }
    }

    override fun receiveData(data: NSData) {
        updateChunks(data)
    }

    private fun updateChunks(data: NSData) {
        var newValue = ByteArray(0)
        newValue += chunks.value
        newValue += data.toByteArray()
        chunks.value = newValue.share()
        //chunkFlow.value = newValue.share()
    }

    private fun clear() {
        clearChunks()
        completion = null
    }

    private fun clearChunks() {
      //  chunks.value = ByteArray(0).share()
    }
}
