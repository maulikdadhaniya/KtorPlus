package com.example.ktor_networking

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import java.util.Base64

actual fun getEngine(): HttpClientEngineFactory<*> = CIO

actual fun String.encodeBase64(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
}
