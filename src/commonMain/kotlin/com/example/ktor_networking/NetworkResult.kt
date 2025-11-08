package com.example.ktor_networking

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: NetworkException) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

sealed class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoInternet(message: String = "No internet connection") : NetworkException(message)
    class Timeout(message: String = "Request timeout") : NetworkException(message)
    class ServerError(val code: Int, message: String) : NetworkException(message)
    class ClientError(val code: Int, message: String) : NetworkException(message)
    class Unknown(message: String, cause: Throwable? = null) : NetworkException(message, cause)
    class Serialization(message: String, cause: Throwable? = null) : NetworkException(message, cause)
}
