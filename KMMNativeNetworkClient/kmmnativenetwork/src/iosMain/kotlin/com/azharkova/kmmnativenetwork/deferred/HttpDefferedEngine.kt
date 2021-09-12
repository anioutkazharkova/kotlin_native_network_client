package com.azharkova.kmm_concurrency_sample.deferred

import com.azharkova.kmm_concurrency_sample.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.Foundation.*

class HttpDefferedEngine {

    val engineJob = SupervisorJob()
    val engineScope: CoroutineScope = CoroutineScope(ioDispatcher + engineJob)

    suspend fun request(request: Request): Response {
        val responseReader = FlowResponseReader()//ChannelResponderReader()//DefferedResponseReader()//ChannelResponderReader()//DefferedResponseReader()
        val urlSession =
            NSURLSession.sessionWithConfiguration(
                NSURLSessionConfiguration.defaultSessionConfiguration, responseReader,//.share(),
                delegateQueue = NSOperationQueue.currentQueue()
            )

        val urlRequest =
            NSMutableURLRequest(NSURL.URLWithString(request.url)!!).apply {
                setAllHTTPHeaderFields(request.headers)
                setHTTPMethod(request.method.value)
                setCachePolicy(NSURLRequestReloadIgnoringCacheData)

            }

        val task = urlSession.share().dataTaskWithRequest(urlRequest)
        engineScope.launch {
            task?.resume()
        }
        val response = responseReader.awaitResponse()
        return response
    }
}