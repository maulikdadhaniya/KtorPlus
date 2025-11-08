package com.maulik.ktorplus

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> KtorPlusNetworkResult<T>.onSuccess(action: (T) -> Unit): KtorPlusNetworkResult<T> {
    if (this is KtorPlusNetworkResult.Success) {
        action(data)
    }
    return this
}

fun <T> KtorPlusNetworkResult<T>.onError(action: (KtorPlusNetworkException) -> Unit): KtorPlusNetworkResult<T> {
    if (this is KtorPlusNetworkResult.Error) {
        action(exception)
    }
    return this
}

fun <T> KtorPlusNetworkResult<T>.onLoading(action: () -> Unit): KtorPlusNetworkResult<T> {
    if (this is KtorPlusNetworkResult.Loading) {
        action()
    }
    return this
}

fun <T, R> KtorPlusNetworkResult<T>.map(transform: (T) -> R): KtorPlusNetworkResult<R> {
    return when (this) {
        is KtorPlusNetworkResult.Success -> KtorPlusNetworkResult.Success(transform(data))
        is KtorPlusNetworkResult.Error -> KtorPlusNetworkResult.Error(exception)
        is KtorPlusNetworkResult.Loading -> KtorPlusNetworkResult.Loading
    }
}

fun <T> KtorPlusNetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is KtorPlusNetworkResult.Success -> data
        else -> null
    }
}

fun <T> KtorPlusNetworkResult<T>.getOrThrow(): T {
    return when (this) {
        is KtorPlusNetworkResult.Success -> data
        is KtorPlusNetworkResult.Error -> throw exception
        is KtorPlusNetworkResult.Loading -> throw IllegalStateException("Result is still loading")
    }
}

fun <T> Flow<KtorPlusNetworkResult<T>>.asFlow(): Flow<KtorPlusNetworkResult<T>> = this

suspend fun <T> ktorPlusNetworkFlow(block: suspend () -> T): Flow<KtorPlusNetworkResult<T>> = flow {
    emit(KtorPlusNetworkResult.Loading)
    try {
        val result = block()
        emit(KtorPlusNetworkResult.Success(result))
    } catch (e: Exception) {
        emit(KtorPlusNetworkResult.Error(KtorPlusNetworkException.Unknown(e.message ?: "Unknown error", e)))
    }
}
