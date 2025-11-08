package com.example.ktor_networking

import android.util.Base64
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun getEngine(): HttpClientEngineFactory<*> = OkHttp

actual fun String.encodeBase64(): String {
    return Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)
}
