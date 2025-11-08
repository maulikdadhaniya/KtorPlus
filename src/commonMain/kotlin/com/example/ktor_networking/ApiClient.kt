package com.example.ktor_networking

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException

abstract class ApiClient(protected val client: HttpClient) {
    
    protected suspend inline fun <reified T> get(
        path: String,
        params: Map<String, Any?> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): NetworkResult<T> = safeApiCall {
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
    ): NetworkResult<T> = safeApiCall {
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
    ): NetworkResult<T> = safeApiCall {
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
    ): NetworkResult<T> = safeApiCall {
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
    ): NetworkResult<T> = safeApiCall {
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
    ): NetworkResult<T> {
        return try {
            NetworkResult.Success(apiCall())
        } catch (e: UnresolvedAddressException) {
            NetworkResult.Error(NetworkException.NoInternet())
        } catch (e: TimeoutCancellationException) {
            NetworkResult.Error(NetworkException.Timeout())
        } catch (e: SerializationException) {
            NetworkResult.Error(NetworkException.Serialization("Serialization error", e))
        } catch (e: Exception) {
            when (e) {
                is io.ktor.client.plugins.ClientRequestException -> {
                    val statusCode = e.response.status.value
                    NetworkResult.Error(
                        NetworkException.ClientError(
                            statusCode,
                            "Client error: ${e.message}"
                        )
                    )
                }
                is io.ktor.client.plugins.ServerResponseException -> {
                    val statusCode = e.response.status.value
                    NetworkResult.Error(
                        NetworkException.ServerError(
                            statusCode,
                            "Server error: ${e.message}"
                        )
                    )
                }
                else -> NetworkResult.Error(NetworkException.Unknown(e.message ?: "Unknown error", e))
            }
        }
    }
}
