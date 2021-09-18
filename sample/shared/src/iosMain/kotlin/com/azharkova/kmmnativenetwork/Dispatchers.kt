package com.azharkova.kmm_concurrency_sample

import kotlinx.coroutines.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.freeze


val uiDispatcher: CoroutineContext = Dispatchers.Main
val ioDispatcher: CoroutineContext = Dispatchers.Default
val scope = CoroutineScope(ioDispatcher)
val mainScope = MainScope()

@ThreadLocal
object MainDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) {
            try {
                block.run().freeze()
            } catch (err: Throwable) {
                throw err
            }
        }
    }
}