# Migration Guide

This guide helps you migrate from other Pocketbase SDKs or update between versions of the Pocketbase Kotlin SDK.

## From Other SDKs

### From JavaScript SDK

If you're coming from the JavaScript SDK, here are the main differences:

#### Client Creation

**JavaScript:**
```javascript
import PocketBase from 'pocketbase';
const pb = new PocketBase('http://localhost:8090');
```

**Kotlin:**
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

#### Authentication

**JavaScript:**
```javascript
const authData = await pb.collection('users').authWithPassword(
    'user@example.com',
    'password123'
);
```

**Kotlin:**
```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "user@example.com",
    password = "password123"
)

client.login {
    token = authResponse.token
}
```

#### CRUD Operations

**JavaScript:**
```javascript
// Create
const record = await pb.collection('tasks').create({
    title: 'New Task',
    completed: false
});

// Read
const records = await pb.collection('tasks').getList(1, 20, {
    filter: 'completed = false',
    sort: '-created'
});

// Update
const updated = await pb.collection('tasks').update(record.id, {
    completed: true
});

// Delete
await pb.collection('tasks').delete(record.id);
```

**Kotlin:**
```kotlin
@Serializable
data class Task(
    val title: String,
    val completed: Boolean = false
) : Record()

// Create
val record = client.records.create<Task>(
    collection = "tasks",
    body = Task(title = "New Task", completed = false)
)

// Read
val records = client.records.getList<Task>(
    collection = "tasks",
    page = 1,
    perPage = 20,
    filter = Filter("completed = false"),
    sort = Sort("-created")
)

// Update
val updated = client.records.update<Task>(
    collection = "tasks",
    id = record.id,
    body = record.copy(completed = true)
)

// Delete
client.records.delete(collection = "tasks", id = record.id)
```

### From Dart SDK

#### Client Setup

**Dart:**
```dart
import 'package:pocketbase/pocketbase.dart';
final pb = PocketBase('http://localhost:8090');
```

**Kotlin:**
```kotlin
val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTP
        host = "localhost"
        port = 8090
    }
)
```

#### Models

**Dart:**
```dart
class Task {
  final String id;
  final String title;
  final bool completed;
  
  Task({required this.id, required this.title, required this.completed});
}
```

**Kotlin:**
```kotlin
@Serializable
data class Task(
    val title: String,
    val completed: Boolean = false
) : Record() // Extends Record for Pocketbase fields
```

## Version Updates

### From 0.x to 1.0

#### Package Name Change

**Old:**
```kotlin
import io.github.agrevster.pocketbaseKotlin.*
```

**New:**
```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.*
```

#### Client Constructor

**Old:**
```kotlin
val client = PocketbaseClient("http://localhost:8090")
```

**New:**
```kotlin
val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTP
        host = "localhost"
        port = 8090
    }
)
```

#### Authentication Changes

**Old:**
```kotlin
val authData = client.users.authWithPassword("user@example.com", "password")
client.authStore.save(authData.token, authData.record)
```

**New:**
```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "user@example.com",
    password = "password"
)

client.login {
    token = authResponse.token
}
```

#### Query API Changes

**Old:**
```kotlin
val records = client.collection("tasks").getList(
    page = 1,
    perPage = 20,
    filter = "completed = false",
    sort = "-created"
)
```

**New:**
```kotlin
val records = client.records.getList<Task>(
    collection = "tasks",
    page = 1,
    perPage = 20,
    filter = Filter("completed = false"),
    sort = Sort("-created")
)
```

## Breaking Changes

### Version 1.0.0

1. **Package structure changed** - All imports need to be updated
2. **Client constructor** - Now uses DSL for URL configuration
3. **Authentication flow** - Separate login step required
4. **Query DSL** - Filters and sorts now use dedicated classes
5. **Type safety** - All operations require explicit type parameters

### Migration Steps

1. **Update dependencies:**
   ```kotlin
   dependencies {
       implementation("dev.abd3lraouf:pocketbase-kotlin:1.0.0")
   }
   ```

2. **Update imports:**
   ```kotlin
   // Replace all old imports
   import io.github.agrevster.pocketbaseKotlin.*
   
   // With new imports
   import dev.abd3lraouf.libs.pocketbase.kotlin.*
   ```

3. **Update client creation:**
   ```kotlin
   val client = PocketbaseClient(
       baseUrl = {
           protocol = URLProtocol.HTTP
           host = "localhost"
           port = 8090
       }
   )
   ```

4. **Update authentication:**
   ```kotlin
   val authResponse = client.records.authWithPassword<AuthRecord>(
       collection = "users",
       email = "user@example.com",
       password = "password"
   )
   
   client.login {
       token = authResponse.token
   }
   ```

5. **Update queries:**
   ```kotlin
   val records = client.records.getList<Task>(
       collection = "tasks",
       filter = Filter("completed = false"),
       sort = Sort("-created")
   )
   ```

## Common Migration Issues

### Issue: Import Errors

**Problem:**
```kotlin
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient // Cannot resolve
```

**Solution:**
```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
```

### Issue: Authentication Not Working

**Problem:**
```kotlin
val authData = client.users.authWithPassword("email", "password")
// API calls fail with 401
```

**Solution:**
```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "email",
    password = "password"
)

client.login {
    token = authResponse.token
}
```

### Issue: Query Syntax Errors

**Problem:**
```kotlin
val records = client.collection("tasks").getList(
    filter = "completed = false" // String not accepted
)
```

**Solution:**
```kotlin
val records = client.records.getList<Task>(
    collection = "tasks",
    filter = Filter("completed = false")
)
```

### Issue: Type Inference Problems

**Problem:**
```kotlin
val record = client.records.create("tasks", task) // Type not inferred
```

**Solution:**
```kotlin
val record = client.records.create<Task>(
    collection = "tasks",
    body = task
)
```

## Best Practices for Migration

1. **Gradual Migration:**
   - Update one module at a time
   - Keep old and new versions side by side during transition
   - Test thoroughly after each update

2. **Type Safety:**
   - Define proper data classes with `@Serializable`
   - Extend `Record` for all Pocketbase models
   - Use explicit type parameters

3. **Error Handling:**
   - Update exception handling for new `PocketbaseException`
   - Handle authentication state properly

4. **Testing:**
   - Update all tests to use new API
   - Test authentication flows thoroughly
   - Verify query results match expected types

## Migration Checklist

- [ ] Update dependency version
- [ ] Update all imports
- [ ] Update client initialization
- [ ] Update authentication code
- [ ] Update query syntax
- [ ] Update model definitions
- [ ] Update error handling
- [ ] Update tests
- [ ] Verify all functionality works

## Getting Help

If you encounter issues during migration:

1. Check the [API Documentation](api.md)
2. Review [Examples](../examples/)
3. Check [GitHub Issues](https://github.com/abd3lraouf/pocketbase-kotlin/issues)
4. Join the [Discussions](https://github.com/abd3lraouf/pocketbase-kotlin/discussions)

## Migration Tools

### Automated Import Updates

You can use IDE find-and-replace to update imports:

**Find:**
```
import io.github.agrevster.pocketbaseKotlin
```

**Replace:**
```
import dev.abd3lraouf.libs.pocketbase.kotlin
```

### Client Constructor Script

Old constructor pattern:
```kotlin
val client = PocketbaseClient("http://localhost:8090")
```

New constructor pattern:
```kotlin
val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTP
        host = "localhost"
        port = 8090
    }
)
```

### Authentication Update Script

Old authentication:
```kotlin
val authData = client.users.authWithPassword("email", "password")
client.authStore.save(authData.token, authData.record)
```

New authentication:
```kotlin
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "email",
    password = "password"
)

client.login {
    token = authResponse.token
}
```