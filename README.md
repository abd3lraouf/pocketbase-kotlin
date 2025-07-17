# Pocketbase Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/dev.abd3lraouf/pocketbase-kotlin)](https://central.sonatype.com/artifact/dev.abd3lraouf/pocketbase-kotlin)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A multiplatform Kotlin SDK for [Pocketbase](https://pocketbase.io) designed to work seamlessly across multiple platforms with full type safety and coroutine support.

## Features

- 🚀 **Kotlin Multiplatform** - Works on Android, iOS, JVM, and native targets
- 🔒 **Type Safe** - Full Kotlin type safety with kotlinx.serialization
- ⚡ **Coroutines** - Built with Kotlin Coroutines for async operations
- 🎯 **DSL Support** - Fluent DSL for queries, authentication, and batch operations
- 📦 **Comprehensive API** - Full support for all Pocketbase API endpoints
- 🔄 **Real-time** - Real-time subscriptions via Server-Sent Events
- 🛡️ **Authentication** - Built-in authentication with token management

## Supported Platforms

| Platform | Support |
|----------|---------|
| Android | ✅ |
| iOS (ARM64) | ✅ |
| iOS (x64) | ✅ |
| iOS Simulator (ARM64) | ✅ |

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("dev.abd3lraouf:pocketbase-kotlin:1.0.0")
}
```

**Note:** This library requires the [Kotlinx Serialization plugin](https://github.com/Kotlin/kotlinx.serialization#using-the-plugins-block).

## Quick Start

### 1. Create a Client

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import io.ktor.http.*

val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTP
        host = "localhost"
        port = 8090
    }
)
```

### 2. Authentication

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.login
import dev.abd3lraouf.libs.pocketbase.kotlin.models.AuthRecord

// Authenticate with email and password
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "user@example.com",
    password = "password123"
)

// Login with the received token
client.login {
    token = authResponse.token
}
```

### 3. Working with Records

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.models.Record
import kotlinx.serialization.Serializable

// Define your record model
@Serializable
data class Task(
    val title: String,
    val completed: Boolean = false,
    val description: String? = null
) : Record()

// Create a record
val newTask = Task(title = "Learn Pocketbase Kotlin", completed = false)
val createdTask = client.records.create<Task>(
    collection = "tasks",
    body = newTask
)

// Get records with filtering
val tasks = client.records.getList<Task>(
    collection = "tasks",
    page = 1,
    perPage = 20,
    filter = Filter("completed = false")
)

// Update a record
val updatedTask = client.records.update<Task>(
    collection = "tasks",
    id = createdTask.id,
    body = createdTask.copy(completed = true)
)
```

### 4. Query DSL

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.*

// Advanced querying with DSL
val results = client.records.getList<Task>(
    collection = "tasks",
    page = 1,
    perPage = 10,
    filter = Filter("title ~ 'Learn' && completed = false"),
    sort = Sort("-created", "title"),
    expand = Expand("user", "category"),
    fields = ShowFields("id", "title", "completed", "user.name")
)
```

### 5. Real-time Subscriptions

```kotlin
// Subscribe to real-time changes
client.realtime.subscribe("tasks") { event ->
    when (event.action) {
        "create" -> println("New task created: ${event.record}")
        "update" -> println("Task updated: ${event.record}")
        "delete" -> println("Task deleted: ${event.record}")
    }
}
```

### 6. Batch Operations

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.BatchRequestBuilder

// Perform multiple operations in a single request
val results = client.batch.send {
    create("tasks", Task("Task 1", false))
    create("tasks", Task("Task 2", true))
    update("tasks", "RECORD_ID", Task("Updated Task", true))
    delete("tasks", "RECORD_ID_TO_DELETE")
}
```

## Advanced Usage

### File Uploads

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.FileUpload

@Serializable
data class Post(
    val title: String,
    val content: String,
    val image: String? = null
) : Record()

// Upload file with record
val post = client.records.create<Post>(
    collection = "posts",
    body = mapOf(
        "title" to "My Post".toJsonPrimitive(),
        "content" to "Post content".toJsonPrimitive()
    ),
    files = listOf(
        FileUpload(
            fieldName = "image",
            data = imageByteArray,
            fileName = "image.jpg"
        )
    )
)
```

### Expanding Relations

```kotlin
@Serializable
data class User(val name: String, val email: String) : Record()

@Serializable
data class Post(
    val title: String,
    val content: String,
    val author: String  // User ID
) : ExpandRecord<User>()

// Get posts with expanded user data
val posts = client.records.getList<Post>(
    collection = "posts",
    expandRelations = ExpandRelations("author")
)

// Access expanded data
val authorName = posts.items.first().expand?.get("author")?.name
```

### Authentication Providers

```kotlin
// OAuth2 authentication
val oauthResponse = client.records.authWithOauth2<AuthRecord>(
    collection = "users",
    provider = "google",
    code = "oauth_code",
    codeVerifier = "code_verifier",
    redirectUrl = "https://your-app.com/callback"
)
```

## API Reference

### Services

- **`records`** - CRUD operations for records and authentication
- **`collections`** - Collection management
- **`files`** - File operations
- **`realtime`** - Real-time subscriptions
- **`health`** - Health check endpoint
- **`settings`** - Server settings
- **`backups`** - Backup operations
- **`logs`** - Log management
- **`batch`** - Batch operations

### Models

- **`Record`** - Base record model
- **`AuthRecord`** - Authenticated record model
- **`Collection`** - Collection schema model
- **`User`** - User model
- **`Admin`** - Admin model

### DSL Components

- **`Filter`** - Query filtering
- **`Sort`** - Result sorting
- **`Expand`** - Relation expansion
- **`ShowFields`** - Field selection
- **`BatchRequestBuilder`** - Batch operations

## Documentation

- [API Documentation](docs/api.md)
- [Authentication Guide](docs/authentication.md)
- [Query DSL Guide](docs/query-dsl.md)
- [Real-time Guide](docs/realtime.md)
- [File Upload Guide](docs/file-uploads.md)
- [Migration Guide](docs/migration.md)

## Examples

Check out the [examples](examples/) directory for more detailed usage examples:

- [Basic CRUD Operations](examples/basic-crud.md)
- [Authentication](examples/authentication.md)
- [Real-time Subscriptions](examples/realtime.md)
- [File Uploads](examples/file-uploads.md)
- [Batch Operations](examples/batch-operations.md)

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

- [GitHub Issues](https://github.com/abd3lraouf/pocketbase-kotlin/issues)
- [Discussions](https://github.com/abd3lraouf/pocketbase-kotlin/discussions)
- [Pocketbase Documentation](https://pocketbase.io/docs/)