package com.azharkova.kmm_concurrency_sample

import platform.objc.objc_sync_enter
import platform.objc.objc_sync_exit
import kotlin.native.concurrent.*

class SharedDetachedObject<T:Any>(producer: () -> T) {
    private val adog : AtomicReference<DetachedObjectGraph<Any>?>
    private val lock = Any()

    init {
        val detachedObjectGraph = DetachedObjectGraph { producer() as Any }.freeze()
        adog = AtomicReference(detachedObjectGraph.freeze())
    }

    fun <R> access(block: (T) -> R): R = trySynchronized(lock){
        val holder = FreezableAtomicReference<Any?>(null)
        val producer = { grabAccess(holder, block) as Any }
        adog.value = DetachedObjectGraph(TransferMode.SAFE, producer).freeze()
        val result = holder.value!!
        holder.value = null
        result as R
    }

    private fun <R> grabAccess(holder:FreezableAtomicReference<Any?>, block: (T) -> R):T{
        val attach = adog.value!!.attach()
        val t = attach as T
        holder.value = block(t)
        return t
    }

    fun clear(){
        adog.value?.attach()
    }
}

inline fun <R> trySynchronized(lock: Any?, block: () -> R): R {
    if (lock != null) {
        objc_sync_enter(lock)
        val result = block()
        objc_sync_exit(lock)
        return result
    } else {
        return block()
    }
}