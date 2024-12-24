package com.example.bootstrap.data.repository

import com.example.bootstrap.models.Resource
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random

class AuthRepository @Inject constructor() {

    suspend fun login(email: String, password: String): Resource<String> {
        delay(3_000L)
        val result = Random.nextBoolean()
        if (result) {
            return Resource.Success("Success")
        } else {
            return Resource.Failure(networkError = "Failure")
        }
    }
}