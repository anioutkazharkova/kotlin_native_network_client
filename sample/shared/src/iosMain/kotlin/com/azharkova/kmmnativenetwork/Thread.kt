package com.azharkova.kmm_concurrency_sample

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.*

internal fun <T> T.share(): T {
    return this.freeze()
}
/*
internal fun <T> T.atomic(): AtomicReference<T>{
    return AtomicReference(this.share())
}*/

internal fun <T> T.atomic(): FreezableAtomicReference<T>{
    return FreezableAtomicReference(this)
}

internal fun background(block: () -> Unit) {
    val future = worker.execute(TransferMode.SAFE, { block}) {
        it()
    }
    collectFutures.add(future)
}

internal fun background(block: () -> (Any?), callback: (Any?)->Unit) {
    val future = worker.execute(TransferMode.SAFE, { block }) {
        it()
    }
    future.consume {
        main {
            callback(it)
        }
    }
    collectFutures.add(future)
}


internal fun main(block:()->Unit) {
    block.apply {
        val nonfreezedBlock = this
        dispatch_async(dispatch_get_main_queue()) {
            nonfreezedBlock()
        }
    }
}

private val worker = Worker.start()
private val collectFutures = mutableListOf<Future<*>>()