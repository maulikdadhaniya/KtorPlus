package com.maulik.ktorplus

import io.ktor.client.request.*
import io.ktor.http.*

class KtorPlusRequestBuilder {
    private val headers = mutableMapOf<String, String>()
    private val params = mutableMapOf<String, Any?>()
    
    fun addHeader(key: String, value: String) = apply {
        headers[key] = value
    }
    
    fun addHeaders(vararg pairs: Pair<String, String>) = apply {
        headers.putAll(pairs)
    }
    
    fun addParam(key: String, value: Any?) = apply {
        params[key] = value
    }
    
    fun addParams(vararg pairs: Pair<String, Any?>) = apply {
        params.putAll(pairs)
    }
    
    fun bearerAuth(token: String) = apply {
        headers["Authorization"] = "Bearer $token"
    }
    
    fun basicAuth(username: String, password: String) = apply {
        val credentials = "$username:$password"
        val encoded = credentials.encodeBase64()
        headers["Authorization"] = "Basic $encoded"
    }
    
    fun getHeaders(): Map<String, String> = headers.toMap()
    fun getParams(): Map<String, Any?> = params.toMap()
}

expect fun String.encodeBase64(): String
