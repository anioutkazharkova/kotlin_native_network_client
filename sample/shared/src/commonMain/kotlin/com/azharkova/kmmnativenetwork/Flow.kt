package com.azharkova.kmm_concurrency_sample

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

interface Cancellable {
    fun cancel()
}

class AnyFlow<T>(source: Flow<T>): Flow<T> by source {
    fun collect(onEach: (T) -> Unit, onCompletion: (cause: Throwable?) -> Unit): Cancellable {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        scope.launch {
            try {
                collect {
                    onEach(it)
                }

                onCompletion(null)
            } catch (e: Throwable) {
                onCompletion(e)
            }
        }

        return object : Cancellable {
            override fun cancel() {
                scope.cancel()
            }
        }
    }
}

fun <T> Flow<T>.collect(onEach: (T) -> Unit, onCompletion: (cause: Throwable?) -> Unit): Cancellable {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    scope.launch {
        try {
            collect { onEach(it) }
            onCompletion(null)
        } catch (e: Throwable) {
            onCompletion(e)
        }
    }

    return object : Cancellable {
        override fun cancel() {
            scope.cancel()
        }
    }
}

fun <T> ConflatedBroadcastChannel<T>.wrap(): CFlow<T> = CFlow(asFlow())

fun <T> Flow<T>.wrap(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    fun watch(block: (T) -> Unit): Cancellable {

        origin.onEach {
            block(it)
        }.launchIn(scope)

        return object : Cancellable {
            override fun cancel() {
                scope.cancel()
            }
        }
    }
}

class CombineFlowData<T,K, S> (
    val source1: Flow<T>,
    val source2: Flow<K>,
    private val combineArg: (arg1: T?, arg2: K?) -> S) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    var value: Flow<S>
    init {
        value = source1.combine(source2) { it1, it2 ->
            combineArg(it1, it2)
        }.flowOn(Dispatchers.Main)
    }
}