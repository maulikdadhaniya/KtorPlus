package com.example.ktor_networking

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

actual fun getEngine(): HttpClientEngineFactory<*> = Darwin

@OptIn(ExperimentalForeignApi::class)
actual fun String.encodeBase64(): String {
    val nsString = NSString.create(string = this)
    val data = nsString.dataUsingEncoding(NSUTF8StringEncoding)
    return data?.base64EncodedStringWithOptions(0u) ?: ""
}
