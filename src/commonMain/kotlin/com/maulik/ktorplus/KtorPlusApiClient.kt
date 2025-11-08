package com.maulik.ktorplus

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException

abstract class KtorPlusApiClient(protected val client: HttpClient) {
    
    protected suspend inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): KtorPlusNetworkResult<T> = safeApiCall {
        client.get(path) {
            params.forEach { (key, value) ->
                value?.let { parameter(key, it) }
            }
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.body()
    }
    
    protected suspend inline fun <reified T> post(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): KtorPlusNetworkResult<T> = safeApiCall {
        client.post(path) {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.body()
    }
    
    protected suspend inline fun <reified T> put(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): KtorPlusNetworkResult<T> = safeApiCall {
        client.put(path) {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.body()
    }
    
    protected suspend inline fun <reified T> delete(
        path: String,
        headers: Map<String, String> = emptyMap()
    ): KtorPlusNetworkResult<T> = safeApiCall {
        client.delete(path) {
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.body()
    }
    
    protected suspend inline fun <reified T> patch(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): KtorPlusNetworkResult<T> = safeApiCall {
        client.patch(path) {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.body()
    }
    
    protected suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> T
    ): KtorPlusNetworkResult<T> {
        return try {
            KtorPlusNetworkResult.Success(apiCall())
        } catch (e: UnresolvedAddressException) {
            KtorPlusNetworkResult.Error(KtorPlusNetworkException.NoInternet())
        } catch (e: TimeoutCancellationException) {
            KtorPlusNetworkResult.Error(KtorPlusNetworkException.Timeout())
        } catch (e: SerializationException) {
            KtorPlusNetworkResult.Error(KtorPlusNetworkException.Serialization("Serialization error", e))
        } catch (e: Exception) {
            when (e) {
                is io.ktor.client.plugins.ClientRequestException -> {
                    val statusCode = e.response.status.value
                    KtorPlusNetworkResult.Error(
                        KtorPlusNetworkException.ClientError(
                            statusCode,
                            "Client error: ${e.message}"
                        )
                    )
                }
                is io.ktor.client.plugins.ServerResponseException -> {
                    val statusCode = e.response.status.value
                    KtorPlusNetworkResult.Error(
                        KtorPlusNetworkException.ServerError(
                            statusCode,
                            "Server error: ${e.message}"
                        )
                    )
                }
                else -> KtorPlusNetworkResult.Error(KtorPlusNetworkException.Unknown(e.message ?: "Unknown error", e))
            }
        }
    }
}
