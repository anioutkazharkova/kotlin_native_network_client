package com.azharkova.kmm_network.deferred


import com.azharkova.kmm_network.Request
import com.azharkova.kmm_network.Response
import com.azharkova.kmm_network.ioDispatcher
import com.azharkova.kmm_network.share
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.Foundation.*

class HttpDefferedEngine() {

    val engineJob = SupervisorJob()
    val engineScope: CoroutineScope = CoroutineScope(ioDispatcher + engineJob)
    val responseReader = FlowResponseReader()
    val urlSession =
        NSURLSession.sessionWithConfiguration(
            NSURLSessionConfiguration.defaultSessionConfiguration, responseReader,//.share(),
            delegateQueue = NSOperationQueue.currentQueue().apply { this?.maxConcurrentOperationCount = 1
            }
        )

    suspend fun request(request: Request): Response {
        val urlRequest =
            NSMutableURLRequest(NSURL.URLWithString(request.url)!!).apply {
                setAllHTTPHeaderFields(request.headers)
                setHTTPMethod(request.method.value)
                setTimeoutInterval(300.0)
            }

        val task = urlSession.share().dataTaskWithRequest(urlRequest)
        engineScope.launch {
            task?.resume()
        }
        val response = responseReader.awaitResponse()
        return response
    }
}