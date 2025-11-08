# KtorPlus - Kotlin Multiplatform Networking Library

[![Maven Central](https://img.shields.io/maven-central/v/com.maulik.ktorplus/ktorplus.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.maulik.ktorplus%22%20AND%20a:%22ktorplus%22)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/ktor-3.0.3-orange.svg)](https://ktor.io)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful Kotlin Multiplatform networking library built on top of Ktor client, providing a clean and type-safe API for making HTTP requests across Android, iOS, and JVM platforms.

## ✨ Features

- ✅ **Multiplatform Support**: Works on Android, iOS, and JVM (Desktop)
- ✅ **Type-Safe API**: Strongly typed requests and responses using Kotlin serialization
- ✅ **Error Handling**: Comprehensive error handling with sealed classes
- ✅ **Logging**: Built-in request/response logging
- ✅ **Retry Logic**: Automatic retry on server errors
- ✅ **Timeout Configuration**: Configurable timeouts for requests
- ✅ **Authentication**: Built-in support for Bearer and Basic auth
- ✅ **Extension Functions**: Convenient extension functions for result handling
- ✅ **Flow Support**: Reactive programming with Kotlin Flow
- ✅ **Zero Boilerplate**: Minimal setup required

## 📦 Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.maulik.ktorplus:ktorplus:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.maulik.ktorplus:ktorplus:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.maulik.ktorplus</groupId>
    <artifactId>ktorplus</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🚀 Quick Start

```kotlin
import com.maulik.ktorplus.*
import kotlinx.serialization.Serializable

// 1. Define your data model
@Serializable
data class User(val id: Int, val name: String, val email: String)

// 2. Create API client
class UserApiClient : KtorPlusApiClient(
    KtorPlusClient.create(
        baseUrl = "https://api.example.com",
        enableLogging = true
    )
) {
    suspend fun getUsers(): KtorPlusNetworkResult<List<User>> = get("/users")
    suspend fun getUserById(id: Int): KtorPlusNetworkResult<User> = get("/users/$id")
}

// 3. Make API calls
val apiClient = UserApiClient()
val result = apiClient.getUsers()
    .onSuccess { users -> println("Got ${users.size} users") }
    .onError { error -> println("Error: ${error.message}") }
```

## 📖 Documentation

- **[Complete Usage Guide](../HOW_TO_USE_KTORPLUS.md)** - Comprehensive guide with examples
- **[Publishing Guide](PUBLISHING.md)** - How to publish the library
- **[Changelog](CHANGELOG.md)** - Version history

## 🎯 Core Components

### KtorPlusNetworkResult

Type-safe result wrapper:

```kotlin
when (result) {
    is KtorPlusNetworkResult.Success -> println(result.data)
    is KtorPlusNetworkResult.Error -> println(result.exception)
    is KtorPlusNetworkResult.Loading -> println("Loading...")
}
```

### Error Handling

```kotlin
result.onError { error ->
    when (error) {
        is KtorPlusNetworkException.NoInternet -> // No connection
        is KtorPlusNetworkException.Timeout -> // Request timeout
        is KtorPlusNetworkException.ServerError -> // 5xx errors
        is KtorPlusNetworkException.ClientError -> // 4xx errors
        is KtorPlusNetworkException.Serialization -> // JSON errors
        is KtorPlusNetworkException.Unknown -> // Other errors
    }
}
```

## 🌐 Platform Support

| Platform | Engine | Min Version |
|----------|--------|-------------|
| Android | OkHttp | API 24+ |
| iOS | Darwin | iOS 13+ |
| JVM/Desktop | CIO | JVM 11+ |

## 📄 License

```
Copyright 2024 Maulik Dadhaniya

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

**Made with ❤️ for the Kotlin Multiplatform community**
