# 🔥KtorPlus - Kotlin Multiplatform 🔥
---------------------------------------

# How to Import and Use KtorPlus Module

A comprehensive guide to importing and using the KtorPlus networking module in your Kotlin Multiplatform project.

---

## Table of Contents
1. [Import the Module](#import-the-module)
2. [Basic Setup](#basic-setup)
3. [Creating Your First API Client](#creating-your-first-api-client)
4. [Making API Calls](#making-api-calls)
5. [Error Handling](#error-handling)
6. [Advanced Usage](#advanced-usage)
7. [Integration with Architecture Patterns](#integration-with-architecture-patterns)

---

## Import the Module

### Step 1: Add Module Dependency

In your module's `build.gradle.kts` file, add the KtorPlus dependency:

```kotlin
// In composeApp/build.gradle.kts or any other module
kotlin {
    sourceSets {
        commonMain.dependencies {
            // ... other dependencies
            implementation(project(":KtorPlus"))
        }
    }
}
```

### Step 2: Sync Gradle

After adding the dependency, sync your Gradle project:
```bash
./gradlew build
```

### Step 3: Import in Your Code

```kotlin
import com.maulik.ktorplus.*
```

---

## Basic Setup

### Understanding Core Components

KtorPlus provides these main components:

| Component | Purpose |
|-----------|---------|
| `KtorPlusClient` | Factory for creating HTTP clients |
| `KtorPlusApiClient` | Base class for API clients |
| `KtorPlusNetworkResult<T>` | Wrapper for API responses |
| `KtorPlusNetworkException` | Typed exceptions for errors |
| `KtorPlusRequestBuilder` | Helper for building requests |

---

## Creating Your First API Client

### Step 1: Define Your Data Models

```kotlin
import kotlinx.serialization.Serializable

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
```

### Step 2: Create API Client Class

```kotlin
import com.maulik.ktorplus.KtorPlusApiClient
import com.maulik.ktorplus.KtorPlusClient
import com.maulik.ktorplus.KtorPlusNetworkResult

class UserApiClient : KtorPlusApiClient(
    KtorPlusClient.create(
        baseUrl = "https://api.example.com",
        enableLogging = true,
        timeoutMillis = 30_000
    )
) {
    
    // GET request
    suspend fun getUsers(): KtorPlusNetworkResult<List<User>> {
        return get("/users")
    }
    
    // GET with path parameter
    suspend fun getUserById(id: Int): KtorPlusNetworkResult<User> {
        return get("/users/$id")
    }
    
    // GET with query parameters
    suspend fun searchUsers(query: String): KtorPlusNetworkResult<List<User>> {
        return get(
            path = "/users/search",
            params = mapOf("q" to query)
        )
    }
    
    // POST request
    suspend fun createUser(request: CreateUserRequest): KtorPlusNetworkResult<User> {
        return post("/users", body = request)
    }
    
    // PUT request
    suspend fun updateUser(id: Int, request: CreateUserRequest): KtorPlusNetworkResult<User> {
        return put("/users/$id", body = request)
    }
    
    // DELETE request
    suspend fun deleteUser(id: Int): KtorPlusNetworkResult<Unit> {
        return delete("/users/$id")
    }
}
```

### Step 3: Initialize in Your App

```kotlin
// Create a single instance (can be injected via DI)
val userApiClient = UserApiClient()
```

---

## Making API Calls

### Basic API Call

```kotlin
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

suspend fun fetchUsers() {
    val result = userApiClient.getUsers()
    
    // Handle result
    when (result) {
        is KtorPlusNetworkResult.Success -> {
            println("Got ${result.data.size} users")
        }
        is KtorPlusNetworkResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is KtorPlusNetworkResult.Loading -> {
            println("Loading...")
        }
    }
}
```

### Using Extension Functions

```kotlin
suspend fun fetchUsersWithExtensions() {
    userApiClient.getUsers()
        .onSuccess { users ->
            println("Success! Got ${users.size} users")
            users.forEach { println(it.name) }
        }
        .onError { error ->
            println("Error: ${error.message}")
        }
}
```

### Getting Data Directly

```kotlin
// Get data or null
val users: List<User>? = userApiClient.getUsers().getOrNull()

// Get data or throw exception
try {
    val users: List<User> = userApiClient.getUsers().getOrThrow()
} catch (e: KtorPlusNetworkException) {
    println("Failed: ${e.message}")
}
```

### Transform Results

```kotlin
// Map the result to a different type
val userNames: KtorPlusNetworkResult<List<String>> = userApiClient.getUsers()
    .map { users -> users.map { it.name } }

// Get transformed data
val names: List<String> = userNames.getOrNull() ?: emptyList()
```

---

## Error Handling

### Understanding Error Types

```kotlin
suspend fun handleErrors() {
    val result = userApiClient.getUsers()
    
    result.onError { error ->
        when (error) {
            is KtorPlusNetworkException.NoInternet -> {
                // No internet connection
                showMessage("Please check your internet connection")
            }
            is KtorPlusNetworkException.Timeout -> {
                // Request timed out
                showMessage("Request took too long. Please try again.")
            }
            is KtorPlusNetworkException.ServerError -> {
                // Server error (5xx)
                showMessage("Server error (${error.code}). Try again later.")
            }
            is KtorPlusNetworkException.ClientError -> {
                // Client error (4xx)
                when (error.code) {
                    401 -> showMessage("Please login again")
                    403 -> showMessage("Access denied")
                    404 -> showMessage("Resource not found")
                    else -> showMessage("Request failed (${error.code})")
                }
            }
            is KtorPlusNetworkException.Serialization -> {
                // JSON parsing error
                showMessage("Data format error")
            }
            is KtorPlusNetworkException.Unknown -> {
                // Unknown error
                showMessage("An unexpected error occurred")
            }
        }
    }
}
```

### User-Friendly Error Messages

```kotlin
// Use the helper extension (if you have NetworkUtils.kt)
import com.example.testkmp.network.getUserMessage

suspend fun showUserFriendlyError() {
    userApiClient.getUsers()
        .onError { error ->
            val message = error.getUserMessage()
            showMessage(message)
        }
}
```

---

## Advanced Usage

### 1. Custom Headers

```kotlin
import com.maulik.ktorplus.KtorPlusRequestBuilder

suspend fun fetchWithAuth() {
    val headers = KtorPlusRequestBuilder()
        .bearerAuth("your-token-here")
        .addHeader("X-Custom-Header", "value")
        .getHeaders()
    
    // Note: You'll need to expose headers parameter in your API client
    // Or create a custom method
}
```

### 2. Authentication

```kotlin
class AuthenticatedApiClient(private val token: String) : KtorPlusApiClient(
    KtorPlusClient.create(baseUrl = "https://api.example.com")
) {
    
    private fun authHeaders() = mapOf("Authorization" to "Bearer $token")
    
    suspend fun getProfile(): KtorPlusNetworkResult<User> {
        return get("/profile", headers = authHeaders())
    }
    
    suspend fun updateProfile(user: User): KtorPlusNetworkResult<User> {
        return put("/profile", body = user, headers = authHeaders())
    }
}
```

### 3. Using with Flow

```kotlin
import com.maulik.ktorplus.ktorPlusNetworkFlow
import kotlinx.coroutines.flow.Flow

suspend fun getUsersFlow(): Flow<KtorPlusNetworkResult<List<User>>> {
    return ktorPlusNetworkFlow {
        userApiClient.getUsers().getOrThrow()
    }
}

// In ViewModel
fun loadUsers() {
    viewModelScope.launch {
        getUsersFlow().collect { result ->
            when (result) {
                is KtorPlusNetworkResult.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is KtorPlusNetworkResult.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is KtorPlusNetworkResult.Error -> {
                    _uiState.value = UiState.Error(result.exception.message)
                }
            }
        }
    }
}
```

### 4. Custom JSON Configuration

```kotlin
import kotlinx.serialization.json.Json

class CustomApiClient : KtorPlusApiClient(
    KtorPlusClient.create(
        baseUrl = "https://api.example.com",
        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            encodeDefaults = true
            coerceInputValues = true
        }
    )
) {
    // Your API methods
}
```

### 5. Multiple Base URLs

```kotlin
class MultiServiceClient {
    private val userService = KtorPlusClient.create(
        baseUrl = "https://users.api.com"
    )
    
    private val productService = KtorPlusClient.create(
        baseUrl = "https://products.api.com"
    )
    
    // Use different clients for different services
}
```

---

## Integration with Architecture Patterns

### MVVM Pattern

```kotlin
// ViewModel
class UserViewModel(
    private val userApiClient: UserApiClient
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            userApiClient.getUsers()
                .onSuccess { users ->
                    _uiState.value = UiState.Success(users)
                }
                .onError { error ->
                    _uiState.value = UiState.Error(error.getUserMessage())
                }
        }
    }
}

sealed class UiState {
    data object Loading : UiState()
    data class Success(val users: List<User>) : UiState()
    data class Error(val message: String) : UiState()
}
```

### Repository Pattern

```kotlin
// Repository Interface
interface UserRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
}

// Repository Implementation
class UserRepositoryImpl(
    private val apiClient: UserApiClient
) : UserRepository {
    
    override suspend fun getUsers(): Result<List<User>> {
        return apiClient.getUsers().toResult()
    }
    
    override suspend fun getUserById(id: Int): Result<User> {
        return apiClient.getUserById(id).toResult()
    }
}

// Extension to convert KtorPlusNetworkResult to Result
fun <T> KtorPlusNetworkResult<T>.toResult(): Result<T> {
    return when (this) {
        is KtorPlusNetworkResult.Success -> Result.success(data)
        is KtorPlusNetworkResult.Error -> Result.failure(exception)
        is KtorPlusNetworkResult.Loading -> Result.failure(
            IllegalStateException("Request is still loading")
        )
    }
}
```

### Use Case Pattern

```kotlin
// Use Case
class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getUsers()
    }
}

// In ViewModel
class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    
    fun loadUsers() {
        viewModelScope.launch {
            getUsersUseCase()
                .onSuccess { users ->
                    _uiState.value = UiState.Success(users)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
```

### Dependency Injection (Koin)

```kotlin
import org.koin.dsl.module

val networkModule = module {
    // API Clients
    single { UserApiClient() }
    single { ProductApiClient() }
    
    // Repositories
    single<UserRepository> { UserRepositoryImpl(get()) }
    
    // Use Cases
    single { GetUsersUseCase(get()) }
    
    // ViewModels
    viewModel { UserViewModel(get()) }
}

// In your App
fun Application.setupKoin() {
    startKoin {
        modules(networkModule)
    }
}
```

---

## Complete Example

Here's a complete working example:

```kotlin
// 1. Data Models
@Serializable
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

// 2. API Client
class PostApiClient : KtorPlusApiClient(
    KtorPlusClient.create(
        baseUrl = "https://jsonplaceholder.typicode.com",
        enableLogging = true
    )
) {
    suspend fun getPosts(): KtorPlusNetworkResult<List<Post>> {
        return get("/posts")
    }
    
    suspend fun getPost(id: Int): KtorPlusNetworkResult<Post> {
        return get("/posts/$id")
    }
    
    suspend fun createPost(post: Post): KtorPlusNetworkResult<Post> {
        return post("/posts", body = post)
    }
}

// 3. Repository
interface PostRepository {
    suspend fun getPosts(): Result<List<Post>>
}

class PostRepositoryImpl(
    private val apiClient: PostApiClient
) : PostRepository {
    override suspend fun getPosts(): Result<List<Post>> {
        return apiClient.getPosts().toResult()
    }
}

// 4. ViewModel
class PostViewModel(
    private val repository: PostRepository
) : ViewModel() {
    
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadPosts()
    }
    
    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getPosts()
                .onSuccess { posts ->
                    _posts.value = posts
                }
                .onFailure { error ->
                    _error.value = error.message
                }
            
            _isLoading.value = false
        }
    }
}

// 5. UI (Compose)
@Composable
fun PostListScreen(viewModel: PostViewModel = koinViewModel()) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    when {
        isLoading -> LoadingView()
        error != null -> ErrorView(error!!) { viewModel.loadPosts() }
        else -> PostList(posts)
    }
}
```

---

## Tips and Best Practices

### ✅ Do's

1. **Use sealed classes for UI state**
   ```kotlin
   sealed class UiState {
       data object Loading : UiState()
       data class Success<T>(val data: T) : UiState()
       data class Error(val message: String) : UiState()
   }
   ```

2. **Handle all error types**
   ```kotlin
   result.onError { error ->
       when (error) {
           is KtorPlusNetworkException.NoInternet -> // Handle
           is KtorPlusNetworkException.Timeout -> // Handle
           // ... handle all types
       }
   }
   ```

3. **Use dependency injection**
   ```kotlin
   single { UserApiClient() }
   ```

4. **Create reusable extensions**
   ```kotlin
   fun <T> KtorPlusNetworkResult<T>.toResult(): Result<T>
   ```

### ❌ Don'ts

1. **Don't ignore errors**
   ```kotlin
   // Bad
   val data = apiClient.getData().getOrNull()
   
   // Good
   apiClient.getData()
       .onSuccess { /* handle */ }
       .onError { /* handle */ }
   ```

2. **Don't create multiple client instances**
   ```kotlin
   // Bad
   fun fetchData() {
       val client = UserApiClient() // New instance each time
   }
   
   // Good
   class MyClass(private val client: UserApiClient) // Inject once
   ```

3. **Don't block the main thread**
   ```kotlin
   // Bad
   runBlocking { apiClient.getData() }
   
   // Good
   viewModelScope.launch { apiClient.getData() }
   ```

---

## Troubleshooting

### Build Issues

**Problem**: Module not found
```
Solution: Ensure KtorPlus is included in settings.gradle.kts:
include(":KtorPlus")
```

**Problem**: Unresolved reference
```
Solution: Add dependency in build.gradle.kts:
implementation(project(":KtorPlus"))
```

### Runtime Issues

**Problem**: Serialization error
```
Solution: Ensure your data classes are annotated with @Serializable
```

**Problem**: Timeout errors
```
Solution: Increase timeout in client creation:
KtorPlusClient.create(timeoutMillis = 60_000)
```

---

## Additional Resources

- **Module README**: `KtorPlus/README.md`
- **Integration Guide**: `KTORPLUS_INTEGRATION.md`
- **Examples**: `composeApp/src/commonMain/kotlin/com/example/testkmp/network/DirectNetworkingExample.kt`

---

## Support

For issues or questions:
1. Check the examples in `DirectNetworkingExample.kt`
2. Review the module README
3. Check Ktor documentation: https://ktor.io/

---

**Happy Coding! 🚀**
