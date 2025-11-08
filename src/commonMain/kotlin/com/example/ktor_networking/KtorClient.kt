package com.example.ktor_networking

import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    
    fun create(
        baseUrl: String = "",
        timeoutMillis: Long = 30_000,
        enableLogging: Boolean = true,
        json: Json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    ): HttpClient {
        return HttpClient(getEngine()) {
            
            // Base URL configuration
            if (baseUrl.isNotEmpty()) {
                defaultRequest {
                    url(baseUrl)
                }
            }
            
            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = timeoutMillis
                connectTimeoutMillis = timeoutMillis
                socketTimeoutMillis = timeoutMillis
            }
            
            // Content negotiation with JSON
            install(ContentNegotiation) {
                json(json)
            }
            
            // Logging
            if (enableLogging) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }
            }
            
            // Retry configuration
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                exponentialDelay()
            }
        }
    }
}

expect fun getEngine(): HttpClientEngineFactory<*>
