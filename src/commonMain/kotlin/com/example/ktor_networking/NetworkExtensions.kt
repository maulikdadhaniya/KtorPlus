package com.example.ktor_networking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

fun <T> NetworkResult<T>.onError(action: (NetworkException) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) {
        action(exception)
    }
    return this
}

fun <T> NetworkResult<T>.onLoading(action: () -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Loading) {
        action()
    }
    return this
}

fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(exception)
        is NetworkResult.Loading -> NetworkResult.Loading
    }
}

fun <T> NetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResult.Success -> data
        else -> null
    }
}

fun <T> NetworkResult<T>.getOrThrow(): T {
    return when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.Error -> throw exception
        is NetworkResult.Loading -> throw IllegalStateException("Result is still loading")
    }
}

fun <T> Flow<NetworkResult<T>>.asFlow(): Flow<NetworkResult<T>> = this

suspend fun <T> networkFlow(block: suspend () -> T): Flow<NetworkResult<T>> = flow {
    emit(NetworkResult.Loading)
    try {
        val result = block()
        emit(NetworkResult.Success(result))
    } catch (e: Exception) {
        emit(NetworkResult.Error(NetworkException.Unknown(e.message ?: "Unknown error", e)))
    }
}
