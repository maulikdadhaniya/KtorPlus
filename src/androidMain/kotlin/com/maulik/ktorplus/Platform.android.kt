package com.maulik.ktorplus

import android.util.Base64
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

internal actual fun getEngine(): HttpClientEngineFactory<*> = OkHttp

actual fun String.encodeBase64(): String {
    return Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)
}
