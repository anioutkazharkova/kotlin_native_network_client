package com.azharkova.kmm_concurrency_sample

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import platform.Foundation.NSData

internal fun NSData.toByteArray(): ByteArray {
    val data: CPointer<ByteVar> = bytes!!.reinterpret()
    return ByteArray(length.toInt()) { index -> data[index] }
}

internal fun ByteArray.string():String {
    return this.decodeToString(0,this.size)
}

internal fun ByteArray.add(data: NSData):ByteArray {
    var bytes = this
    bytes += data.toByteArray()
    return bytes
}

internal fun ByteArray.add(data: ByteArray):ByteArray {
    var bytes = this
    bytes += data
    return bytes
}