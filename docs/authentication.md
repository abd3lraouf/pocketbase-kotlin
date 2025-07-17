# Authentication Guide

This guide covers all authentication methods supported by the Pocketbase Kotlin SDK.

## Overview

The SDK provides several authentication methods:
- Email/Password authentication
- OAuth2 authentication
- Token-based authentication
- Admin authentication

## Setup

First, create your Pocketbase client:

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import io.ktor.http.*

val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTPS
        host = "your-pocketbase-instance.com"
    }
)
```

## Email/Password Authentication

### User Authentication

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.models.AuthRecord
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.login

try {
    val authResponse = client.records.authWithPassword<AuthRecord>(
        collection = "users",
        email = "user@example.com",
        password = "password123"
    )
    
    // Login with the received token
    client.login {
        token = authResponse.token
    }
    
    println("Logged in as: ${authResponse.record.email}")
} catch (e: PocketbaseException) {
    println("Authentication failed: ${e.message}")
}
```

### Admin Authentication

```kotlin
try {
    val adminResponse = client.records.authWithPassword<AuthRecord>(
        collection = "_superusers",
        email = "admin@example.com",
        password = "admin123"
    )
    
    client.login {
        token = adminResponse.token
    }
    
    println("Logged in as admin")
} catch (e: PocketbaseException) {
    println("Admin authentication failed: ${e.message}")
}
```

### Custom Identity Field

You can specify a custom identity field for authentication:

```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "john_doe",  // Username instead of email
    password = "password123",
    identityField = "username"
)
```

## OAuth2 Authentication

### Setup OAuth2 Flow

```kotlin
// Step 1: Redirect user to OAuth2 provider
val authUrl = "https://accounts.google.com/oauth/authorize?..."

// Step 2: Handle callback and extract code
val code = "oauth_code_from_callback"
val codeVerifier = "code_verifier_from_pkce"

// Step 3: Authenticate with Pocketbase
try {
    val oauthResponse = client.records.authWithOauth2<AuthRecord>(
        collection = "users",
        provider = "google",
        code = code,
        codeVerifier = codeVerifier,
        redirectUrl = "https://your-app.com/oauth/callback"
    )
    
    client.login {
        token = oauthResponse.token
    }
    
    println("OAuth2 login successful")
} catch (e: PocketbaseException) {
    println("OAuth2 authentication failed: ${e.message}")
}
```

### Supported Providers

Common OAuth2 providers supported by Pocketbase:
- Google (`"google"`)
- GitHub (`"github"`)
- Facebook (`"facebook"`)
- Twitter (`"twitter"`)
- Microsoft (`"microsoft"`)
- And more...

## Token-Based Authentication

### Manual Token Login

If you already have a valid token:

```kotlin
client.login {
    token = "your-jwt-token"
}
```

### Token Validation

Check if the current token is valid:

```kotlin
if (client.authStore.isValid) {
    println("User is authenticated")
    println("Token: ${client.authStore.token}")
} else {
    println("User needs to authenticate")
}
```

## Authentication State Management

### Auth Store

The `BaseAuthStore` manages authentication state:

```kotlin
// Get current auth data
val authStore = client.authStore

// Check if authenticated
if (authStore.isValid) {
    val token = authStore.token
    // Use token for API calls
}

// Clear authentication
authStore.clear()
```

### Custom Auth Store

You can implement a custom auth store for persistence:

```kotlin
class PersistentAuthStore : BaseAuthStore() {
    override fun save(token: String?) {
        super.save(token)
        // Save to local storage, preferences, etc.
        saveToLocalStorage(token)
    }
    
    override fun clear() {
        super.clear()
        // Clear from local storage
        clearFromLocalStorage()
    }
}

val client = PocketbaseClient(
    baseUrl = { /* ... */ },
    store = PersistentAuthStore()
)
```

## Logout

### Simple Logout

```kotlin
client.logout()
```

### Complete Logout with Server

```kotlin
try {
    // Optionally call server logout endpoint
    client.records.logout()
    
    // Clear local auth state
    client.logout()
    
    println("Logged out successfully")
} catch (e: PocketbaseException) {
    println("Logout failed: ${e.message}")
}
```

## Advanced Authentication

### Custom User Model

Define a custom user model with additional fields:

```kotlin
@Serializable
data class CustomUser(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val avatar: String? = null
) : AuthRecord()

// Use with authentication
val authResponse = client.records.authWithPassword<CustomUser>(
    collection = "users",
    email = "user@example.com",
    password = "password123"
)

val user = authResponse.record
println("Welcome, ${user.firstName} ${user.lastName}!")
```

### Expanded Authentication

Get additional related data during authentication:

```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "user@example.com",
    password = "password123",
    expandRelations = ExpandRelations("profile", "roles"),
    fields = ShowFields("*", "profile.*", "roles.*")
)
```

## Error Handling

### Common Authentication Errors

```kotlin
try {
    val authResponse = client.records.authWithPassword<AuthRecord>(
        collection = "users",
        email = "user@example.com",
        password = "wrong_password"
    )
} catch (e: PocketbaseException) {
    when (e.statusCode) {
        400 -> println("Invalid credentials")
        404 -> println("User not found")
        429 -> println("Too many login attempts")
        else -> println("Authentication error: ${e.message}")
    }
}
```

### Token Expiration

Handle token expiration:

```kotlin
try {
    val records = client.records.getList<Task>("tasks")
} catch (e: PocketbaseException) {
    if (e.statusCode == 401) {
        // Token expired, re-authenticate
        client.logout()
        // Redirect to login screen
    }
}
```

## Best Practices

1. **Secure Token Storage**: Store tokens securely using platform-specific secure storage
2. **Token Refresh**: Implement token refresh logic for long-lived applications
3. **Error Handling**: Always handle authentication errors gracefully
4. **Logout on App Exit**: Clear authentication state when the app is closed
5. **Validation**: Validate tokens before making API calls

## Example: Complete Authentication Flow

```kotlin
class AuthManager(private val client: PocketbaseClient) {
    
    suspend fun login(email: String, password: String): Result<AuthRecord> {
        return try {
            val authResponse = client.records.authWithPassword<AuthRecord>(
                collection = "users",
                email = email,
                password = password
            )
            
            client.login {
                token = authResponse.token
            }
            
            Result.success(authResponse.record)
        } catch (e: PocketbaseException) {
            Result.failure(e)
        }
    }
    
    suspend fun loginWithOAuth(provider: String, code: String, codeVerifier: String): Result<AuthRecord> {
        return try {
            val authResponse = client.records.authWithOauth2<AuthRecord>(
                collection = "users",
                provider = provider,
                code = code,
                codeVerifier = codeVerifier,
                redirectUrl = "https://your-app.com/callback"
            )
            
            client.login {
                token = authResponse.token
            }
            
            Result.success(authResponse.record)
        } catch (e: PocketbaseException) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        client.logout()
    }
    
    fun isAuthenticated(): Boolean {
        return client.authStore.isValid
    }
    
    fun getCurrentToken(): String? {
        return client.authStore.token
    }
}
```