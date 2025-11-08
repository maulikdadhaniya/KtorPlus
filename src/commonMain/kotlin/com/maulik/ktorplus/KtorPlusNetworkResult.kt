package com.maulik.ktorplus

sealed class KtorPlusNetworkResult<out T> {
    data class Success<T>(val data: T) : KtorPlusNetworkResult<T>()
    data class Error(val exception: KtorPlusNetworkException) : KtorPlusNetworkResult<Nothing>()
    data object Loading : KtorPlusNetworkResult<Nothing>()
}

sealed class KtorPlusNetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoInternet(message: String = "No internet connection") : KtorPlusNetworkException(message)
    class Timeout(message: String = "Request timeout") : KtorPlusNetworkException(message)
    class ServerError(val code: Int, message: String) : KtorPlusNetworkException(message)
    class ClientError(val code: Int, message: String) : KtorPlusNetworkException(message)
    class Unknown(message: String, cause: Throwable? = null) : KtorPlusNetworkException(message, cause)
    class Serialization(message: String, cause: Throwable? = null) : KtorPlusNetworkException(message, cause)
}
