# API Reference

## PocketbaseClient

The main client class that provides access to all Pocketbase services.

### Constructor

```kotlin
PocketbaseClient(
    baseUrl: URLBuilder.() -> Unit,
    lang: String = "en-US",
    store: BaseAuthStore = BaseAuthStore(null)
)
```

### Properties

- `authStore: BaseAuthStore` - Authentication store for managing tokens
- `records: RecordsService` - CRUD operations for records and authentication
- `collections: CollectionService` - Collection management
- `files: FilesService` - File operations
- `realtime: RealtimeService` - Real-time subscriptions
- `health: HealthService` - Health check endpoint
- `settings: SettingsService` - Server settings
- `backups: BackupsService` - Backup operations
- `logs: LogService` - Log management
- `batch: BatchService` - Batch operations

## Services

### RecordsService

Handles CRUD operations for records and authentication.

#### Methods

##### Authentication

```kotlin
suspend fun <T : AuthRecord> authWithPassword(
    collection: String,
    email: String,
    password: String,
    identityField: String? = null,
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): AuthResponse<T>

suspend fun <T : AuthRecord> authWithOauth2(
    collection: String,
    provider: String,
    code: String,
    codeVerifier: String,
    redirectUrl: String,
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): AuthResponse<T>
```

##### CRUD Operations

```kotlin
suspend fun <T : Record> create(
    collection: String,
    body: T,
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): T

suspend fun <T : Record> create(
    collection: String,
    body: Map<String, JsonPrimitive>,
    files: List<FileUpload> = emptyList(),
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): T

suspend fun <T : Record> getOne(
    collection: String,
    id: String,
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): T

suspend fun <T : Record> getList(
    collection: String,
    page: Int = 1,
    perPage: Int = 30,
    filter: Filter = Filter(),
    sort: Sort = Sort(),
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): ListResult<T>

suspend fun <T : Record> update(
    collection: String,
    id: String,
    body: T,
    expandRelations: ExpandRelations = ExpandRelations(),
    fields: ShowFields = ShowFields()
): T

suspend fun delete(
    collection: String,
    id: String
): Boolean
```

### CollectionService

Manages collection schemas and metadata.

```kotlin
suspend fun <T : Collection> create(collection: T): T
suspend fun <T : Collection> getOne(id: String): T
suspend fun <T : Collection> getList(
    page: Int = 1,
    perPage: Int = 30,
    filter: Filter = Filter(),
    sort: Sort = Sort()
): ListResult<T>
suspend fun <T : Collection> update(id: String, collection: T): T
suspend fun delete(id: String): Boolean
```

### RealtimeService

Handles real-time subscriptions via Server-Sent Events.

```kotlin
suspend fun subscribe(
    topic: String,
    callback: (RealtimeEvent) -> Unit
)
suspend fun unsubscribe(topic: String)
```

### BatchService

Performs multiple operations in a single request.

```kotlin
suspend fun send(block: BatchRequestBuilder.() -> Unit): BatchResult
```

### FileUpload

Represents a file to be uploaded.

```kotlin
data class FileUpload(
    val fieldName: String,
    val data: ByteArray,
    val fileName: String
)
```

## Models

### Record

Base class for all Pocketbase records.

```kotlin
@Serializable
open class Record(
    @Transient private val recordId: String? = null
) : BaseModel(recordId) {
    val collectionId: String?
    val collectionName: String?
}
```

### AuthRecord

Extends Record for authenticated records.

```kotlin
@Serializable
open class AuthRecord : Record() {
    val email: String?
    val verified: Boolean?
    val username: String?
}
```

### Collection

Represents a Pocketbase collection schema.

```kotlin
@Serializable
data class Collection(
    val name: String,
    val type: CollectionType,
    val schema: List<SchemaField>,
    val system: Boolean = false
) : BaseModel()
```

## DSL Components

### Filter

For filtering query results.

```kotlin
data class Filter(val expression: String? = null)

// Usage
Filter("title ~ 'Learn' && completed = false")
```

### Sort

For sorting query results.

```kotlin
data class Sort(val fields: List<String> = emptyList())

// Usage
Sort("-created", "title")  // Descending by created, ascending by title
```

### ExpandRelations

For expanding related records.

```kotlin
data class ExpandRelations(val relations: List<String> = emptyList())

// Usage
ExpandRelations("author", "category")
```

### ShowFields

For selecting specific fields.

```kotlin
data class ShowFields(val fields: List<String> = emptyList())

// Usage
ShowFields("id", "title", "author.name")
```

### BatchRequestBuilder

DSL for building batch requests.

```kotlin
class BatchRequestBuilder {
    fun create(collectionId: String, body: JsonObject, files: List<FileUpload> = emptyList())
    fun update(collectionId: String, id: String, body: JsonObject, files: List<FileUpload> = emptyList())
    fun upsert(collectionId: String, body: JsonObject, files: List<FileUpload> = emptyList())
    fun delete(collectionId: String, id: String)
}
```

## Error Handling

The SDK throws `PocketbaseException` for API errors:

```kotlin
try {
    val record = client.records.getOne<Task>("tasks", "invalid-id")
} catch (e: PocketbaseException) {
    println("Error: ${e.message}")
    println("Status code: ${e.statusCode}")
}
```

## Authentication

### Login DSL

```kotlin
client.login {
    token = "your-auth-token"
}
```

### Logout

```kotlin
client.logout()
```

### Token Management

The `BaseAuthStore` automatically manages tokens:

```kotlin
// Check if authenticated
if (client.authStore.isValid) {
    // Token is valid
}

// Get current token
val token = client.authStore.token

// Clear authentication
client.authStore.clear()
```