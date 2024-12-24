package com.example.bootstrap.models

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Success<T>(data: T): Resource<T>(data)
    class Failure<T>(networkError: String): Resource<T>(error = networkError)
}
