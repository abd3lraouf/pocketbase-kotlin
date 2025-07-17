# Authentication Examples

This example demonstrates various authentication methods available in the Pocketbase Kotlin SDK.

## Basic Setup

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.models.AuthRecord
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.login
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.logout
import io.ktor.http.*
import kotlinx.serialization.Serializable

val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTPS
        host = "your-pocketbase-instance.com"
    }
)
```

## User Authentication

### Simple Email/Password Login

```kotlin
suspend fun simpleLogin() {
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
        
        println("Successfully logged in!")
        println("User ID: ${authResponse.record.id}")
        println("Email: ${authResponse.record.email}")
        println("Token: ${authResponse.token}")
        
    } catch (e: PocketbaseException) {
        println("Login failed: ${e.message}")
    }
}
```

### Login with Custom User Model

```kotlin
@Serializable
data class CustomUser(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val avatar: String? = null,
    val role: String = "user"
) : AuthRecord()

suspend fun loginWithCustomUser() {
    try {
        val authResponse = client.records.authWithPassword<CustomUser>(
            collection = "users",
            email = "john@example.com",
            password = "password123"
        )
        
        client.login {
            token = authResponse.token
        }
        
        val user = authResponse.record
        println("Welcome, ${user.firstName} ${user.lastName}!")
        println("Username: ${user.username}")
        println("Role: ${user.role}")
        
    } catch (e: PocketbaseException) {
        println("Login failed: ${e.message}")
    }
}
```

### Login with Username

```kotlin
suspend fun loginWithUsername() {
    try {
        val authResponse = client.records.authWithPassword<AuthRecord>(
            collection = "users",
            email = "john_doe", // Using username instead of email
            password = "password123",
            identityField = "username"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("Logged in with username successfully!")
        
    } catch (e: PocketbaseException) {
        println("Username login failed: ${e.message}")
    }
}
```

## Admin Authentication

### Admin Login

```kotlin
suspend fun adminLogin() {
    try {
        val authResponse = client.records.authWithPassword<AuthRecord>(
            collection = "_superusers",
            email = "admin@example.com",
            password = "admin123"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("Admin logged in successfully!")
        println("Admin ID: ${authResponse.record.id}")
        
    } catch (e: PocketbaseException) {
        println("Admin login failed: ${e.message}")
    }
}
```

## OAuth2 Authentication

### Google OAuth2

```kotlin
suspend fun loginWithGoogle() {
    try {
        // These would come from your OAuth2 flow
        val authCode = "auth_code_from_google"
        val codeVerifier = "code_verifier_from_pkce"
        
        val authResponse = client.records.authWithOauth2<AuthRecord>(
            collection = "users",
            provider = "google",
            code = authCode,
            codeVerifier = codeVerifier,
            redirectUrl = "https://your-app.com/oauth/callback"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("Google OAuth2 login successful!")
        println("User: ${authResponse.record.email}")
        
    } catch (e: PocketbaseException) {
        println("Google OAuth2 login failed: ${e.message}")
    }
}
```

### GitHub OAuth2

```kotlin
suspend fun loginWithGitHub() {
    try {
        val authCode = "auth_code_from_github"
        val codeVerifier = "code_verifier_from_pkce"
        
        val authResponse = client.records.authWithOauth2<AuthRecord>(
            collection = "users",
            provider = "github",
            code = authCode,
            codeVerifier = codeVerifier,
            redirectUrl = "https://your-app.com/oauth/callback"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("GitHub OAuth2 login successful!")
        
    } catch (e: PocketbaseException) {
        println("GitHub OAuth2 login failed: ${e.message}")
    }
}
```

## Token Management

### Manual Token Login

```kotlin
suspend fun loginWithToken() {
    val savedToken = loadTokenFromStorage() // Your token storage logic
    
    if (savedToken != null) {
        client.login {
            token = savedToken
        }
        
        // Verify token is still valid
        if (client.authStore.isValid) {
            println("Token login successful!")
        } else {
            println("Token is invalid or expired")
            // Redirect to login screen
        }
    } else {
        println("No saved token found")
    }
}
```

### Token Refresh

```kotlin
suspend fun refreshTokenIfNeeded() {
    if (!client.authStore.isValid) {
        println("Token expired, refreshing...")
        
        try {
            // Re-authenticate with stored credentials
            val authResponse = client.records.authWithPassword<AuthRecord>(
                collection = "users",
                email = getStoredEmail(),
                password = getStoredPassword()
            )
            
            client.login {
                token = authResponse.token
            }
            
            saveTokenToStorage(authResponse.token)
            println("Token refreshed successfully")
            
        } catch (e: PocketbaseException) {
            println("Token refresh failed: ${e.message}")
            // Redirect to login screen
        }
    }
}
```

## Authentication State Management

### Complete Auth Manager

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
            
            // Save token for persistence
            saveTokenToStorage(authResponse.token)
            
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
            
            saveTokenToStorage(authResponse.token)
            
            Result.success(authResponse.record)
        } catch (e: PocketbaseException) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        try {
            // Optional: Call server logout endpoint
            // client.records.logout()
            
            // Clear local auth state
            client.logout()
            
            // Clear persisted token
            clearTokenFromStorage()
            
            println("Logged out successfully")
        } catch (e: PocketbaseException) {
            println("Logout error: ${e.message}")
        }
    }
    
    fun isAuthenticated(): Boolean {
        return client.authStore.isValid
    }
    
    fun getCurrentToken(): String? {
        return client.authStore.token
    }
    
    suspend fun getCurrentUser(): AuthRecord? {
        return if (isAuthenticated()) {
            try {
                // Get current user info
                client.records.getOne<AuthRecord>(
                    collection = "users",
                    id = client.authStore.model?.id ?: return null
                )
            } catch (e: PocketbaseException) {
                null
            }
        } else {
            null
        }
    }
    
    suspend fun restoreSession(): Boolean {
        val savedToken = loadTokenFromStorage()
        return if (savedToken != null) {
            client.login {
                token = savedToken
            }
            client.authStore.isValid
        } else {
            false
        }
    }
    
    private fun saveTokenToStorage(token: String) {
        // Implement your token storage logic
        // localStorage.setItem("auth_token", token)
    }
    
    private fun loadTokenFromStorage(): String? {
        // Implement your token loading logic
        // return localStorage.getItem("auth_token")
        return null
    }
    
    private fun clearTokenFromStorage() {
        // Implement your token clearing logic
        // localStorage.removeItem("auth_token")
    }
}
```

## Error Handling

### Authentication Error Handling

```kotlin
suspend fun handleAuthErrors() {
    try {
        val authResponse = client.records.authWithPassword<AuthRecord>(
            collection = "users",
            email = "user@example.com",
            password = "wrong_password"
        )
        
        client.login {
            token = authResponse.token
        }
        
    } catch (e: PocketbaseException) {
        when (e.statusCode) {
            400 -> {
                println("Invalid credentials provided")
                // Show error message to user
            }
            404 -> {
                println("User not found")
                // Suggest account creation
            }
            429 -> {
                println("Too many login attempts")
                // Show rate limit message
            }
            500 -> {
                println("Server error")
                // Show "try again later" message
            }
            else -> {
                println("Login failed: ${e.message}")
            }
        }
    }
}
```

### Network Error Handling

```kotlin
suspend fun handleNetworkErrors() {
    try {
        val authResponse = client.records.authWithPassword<AuthRecord>(
            collection = "users",
            email = "user@example.com",
            password = "password123"
        )
        
        client.login {
            token = authResponse.token
        }
        
    } catch (e: PocketbaseException) {
        println("Authentication failed: ${e.message}")
    } catch (e: Exception) {
        when (e) {
            is ConnectException -> {
                println("Network connection failed")
                // Show offline message
            }
            is SocketTimeoutException -> {
                println("Request timeout")
                // Show timeout message
            }
            else -> {
                println("Unexpected error: ${e.message}")
            }
        }
    }
}
```

## Session Management

### Auto-Login on App Start

```kotlin
class AppStartup(private val authManager: AuthManager) {
    
    suspend fun initializeAuth(): Boolean {
        return try {
            // Try to restore previous session
            val restored = authManager.restoreSession()
            
            if (restored) {
                println("Session restored successfully")
                true
            } else {
                println("No valid session found")
                false
            }
        } catch (e: Exception) {
            println("Session restoration failed: ${e.message}")
            false
        }
    }
}
```

### Session Validation

```kotlin
suspend fun validateSession() {
    if (!client.authStore.isValid) {
        println("Session expired")
        // Redirect to login
        return
    }
    
    try {
        // Test API call to verify token
        val user = client.records.getOne<AuthRecord>(
            collection = "users",
            id = client.authStore.model?.id ?: return
        )
        
        println("Session valid for: ${user.email}")
    } catch (e: PocketbaseException) {
        if (e.statusCode == 401) {
            println("Session expired")
            client.logout()
            // Redirect to login
        }
    }
}
```

## Multi-Collection Authentication

### Different User Types

```kotlin
@Serializable
data class AdminUser(
    val email: String,
    val role: String = "admin",
    val permissions: List<String> = emptyList()
) : AuthRecord()

@Serializable
data class CustomerUser(
    val email: String,
    val name: String,
    val plan: String = "free"
) : AuthRecord()

suspend fun loginAsAdmin() {
    try {
        val authResponse = client.records.authWithPassword<AdminUser>(
            collection = "admins",
            email = "admin@example.com",
            password = "admin123"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("Admin logged in with permissions: ${authResponse.record.permissions}")
        
    } catch (e: PocketbaseException) {
        println("Admin login failed: ${e.message}")
    }
}

suspend fun loginAsCustomer() {
    try {
        val authResponse = client.records.authWithPassword<CustomerUser>(
            collection = "customers",
            email = "customer@example.com",
            password = "customer123"
        )
        
        client.login {
            token = authResponse.token
        }
        
        println("Customer logged in with plan: ${authResponse.record.plan}")
        
    } catch (e: PocketbaseException) {
        println("Customer login failed: ${e.message}")
    }
}
```

## Running the Examples

```kotlin
suspend fun main() {
    val authManager = AuthManager(client)
    
    // Try to restore previous session
    if (!authManager.restoreSession()) {
        // No valid session, try to login
        val loginResult = authManager.login("user@example.com", "password123")
        
        if (loginResult.isSuccess) {
            println("Login successful!")
        } else {
            println("Login failed: ${loginResult.exceptionOrNull()?.message}")
            return
        }
    }
    
    // Now you can make authenticated requests
    val user = authManager.getCurrentUser()
    println("Current user: ${user?.email}")
    
    // Logout when done
    authManager.logout()
}
```