package com.azharkova.kmmconcurrency

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}