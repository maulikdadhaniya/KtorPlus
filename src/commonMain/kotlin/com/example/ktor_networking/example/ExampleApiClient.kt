package com.example.ktor_networking.example

import com.example.ktor_networking.ApiClient
import com.example.ktor_networking.KtorClient
import com.example.ktor_networking.NetworkResult
import kotlinx.serialization.Serializable

/**
 * Example API client demonstrating how to use the ktor_networking module
 * 
 * Usage:
 * ```
 * val apiClient = ExampleApiClient("https://api.example.com")
 * val result = apiClient.getUsers()
 * result.onSuccess { users ->
 *     println("Got ${users.size} users")
 * }.onError { error ->
 *     println("Error: ${error.message}")
 * }
 * ```
 */
class ExampleApiClient(baseUrl: String) : ApiClient(
    KtorClient.create(
        baseUrl = baseUrl,
        enableLogging = true
    )
) {
    
    suspend fun getUsers(): NetworkResult<List<User>> {
        return get("/users")
    }
    
    suspend fun getUserById(id: Int): NetworkResult<User> {
        return get("/users/$id")
    }
    
    suspend fun createUser(user: CreateUserRequest): NetworkResult<User> {
        return post("/users", body = user)
    }
    
    suspend fun updateUser(id: Int, user: UpdateUserRequest): NetworkResult<User> {
        return put("/users/$id", body = user)
    }
    
    suspend fun deleteUser(id: Int): NetworkResult<Unit> {
        return delete("/users/$id")
    }
}

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null
)
