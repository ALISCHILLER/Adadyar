package com.msa.adadyar.core.common

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    inline fun onSuccess(block: (T) -> Unit): AppResult<T> = also {
        if (this is Success) {
            block(data)
        }
    }

    inline fun onFailure(block: (AppError) -> Unit): AppResult<T> = also {
        if (this is Failure) {
            block(error)
        }
    }
}

sealed interface AppError {
    val message: String

    data class NotFound(override val message: String) : AppError
    data class Serialization(override val message: String, val raw: String? = null) : AppError
    data class Validation(override val message: String) : AppError
    data class Storage(override val message: String, val cause: Throwable? = null) : AppError
    data class Unknown(override val message: String, val cause: Throwable? = null) : AppError
}

fun <T> T.toSuccess(): AppResult<T> = AppResult.Success(this)

fun Throwable.toAppError(defaultMessage: String = localizedMessage ?: message ?: "خطای ناشناخته"):
        AppError = when (this) {
    is kotlinx.serialization.SerializationException -> AppError.Serialization(defaultMessage, message)
    else -> AppError.Unknown(defaultMessage, this)
}