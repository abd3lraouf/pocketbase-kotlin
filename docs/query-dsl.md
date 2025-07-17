# Query DSL Guide

The Pocketbase Kotlin SDK provides a powerful DSL for building complex queries with type safety and fluent syntax.

## Overview

The query DSL consists of several components:
- `Filter` - For filtering records
- `Sort` - For sorting results
- `Expand` - For expanding related records
- `ShowFields` - For selecting specific fields

## Filtering

### Basic Filtering

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Filter

// Simple equality
val filter = Filter("status = 'active'")

// Use with getList
val records = client.records.getList<Task>(
    collection = "tasks",
    filter = filter
)
```

### Filter Operators

#### Comparison Operators

```kotlin
// Equality
Filter("age = 25")
Filter("status = 'completed'")

// Not equal
Filter("status != 'deleted'")

// Greater than / Less than
Filter("age > 18")
Filter("age < 65")
Filter("age >= 21")
Filter("age <= 100")

// Like (case-insensitive)
Filter("title ~ 'important'")

// Not like
Filter("title !~ 'spam'")
```

#### Logical Operators

```kotlin
// AND
Filter("status = 'active' && age > 18")

// OR
Filter("status = 'active' || status = 'pending'")

// NOT
Filter("!(status = 'deleted')")

// Complex combinations
Filter("(status = 'active' || status = 'pending') && age > 18")
```

#### Array/List Operations

```kotlin
// In array
Filter("id in ('id1', 'id2', 'id3')")

// Not in array
Filter("status not in ('deleted', 'archived')")

// Array contains
Filter("tags contains 'kotlin'")

// Array not contains
Filter("tags not contains 'deprecated'")
```

#### Date/Time Filtering

```kotlin
// Date comparison
Filter("created >= '2024-01-01'")
Filter("updated < '2024-12-31'")

// Date range
Filter("created >= '2024-01-01' && created <= '2024-12-31'")
```

#### Null Checks

```kotlin
// Is null
Filter("description = null")

// Is not null
Filter("description != null")
```

### Advanced Filtering Examples

```kotlin
// Complex task filtering
val taskFilter = Filter("""
    (status = 'active' || status = 'in_progress') &&
    priority in ('high', 'medium') &&
    assignee != null &&
    due_date >= '2024-01-01' &&
    title ~ 'urgent'
""".trimIndent())

// User filtering with related data
val userFilter = Filter("""
    verified = true &&
    created >= '2024-01-01' &&
    profile.department = 'engineering' &&
    roles.name in ('admin', 'moderator')
""".trimIndent())
```

## Sorting

### Basic Sorting

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Sort

// Single field ascending
val sort = Sort("created")

// Single field descending
val sort = Sort("-created")

// Multiple fields
val sort = Sort("-priority", "created", "title")
```

### Sorting Examples

```kotlin
// Sort tasks by priority (desc) then by created date (asc)
val records = client.records.getList<Task>(
    collection = "tasks",
    sort = Sort("-priority", "created")
)

// Sort users by last name, then first name
val users = client.records.getList<User>(
    collection = "users",
    sort = Sort("lastName", "firstName")
)

// Sort by related field
val posts = client.records.getList<Post>(
    collection = "posts",
    sort = Sort("-author.name", "-created")
)
```

## Expanding Relations

### Basic Expansion

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Expand

// Single relation
val expand = Expand("author")

// Multiple relations
val expand = Expand("author", "category", "tags")
```

### Nested Expansion

```kotlin
// Expand nested relations
val expand = Expand("author.profile", "category.parent")

// Complex expansion
val expand = Expand(
    "author",
    "author.profile",
    "author.roles",
    "category",
    "category.parent",
    "tags"
)
```

### Using Expanded Data

```kotlin
@Serializable
data class User(val name: String, val email: String) : Record()

@Serializable
data class Post(
    val title: String,
    val content: String,
    val author: String  // User ID
) : ExpandRecord<User>()

val posts = client.records.getList<Post>(
    collection = "posts",
    expand = Expand("author")
)

// Access expanded data
posts.items.forEach { post ->
    val author = post.expand?.get("author")
    println("Post: ${post.title} by ${author?.name}")
}
```

### Multiple Relation Types

```kotlin
@Serializable
data class Task(
    val title: String,
    val assignee: String,     // User ID
    val watchers: List<String> // List of User IDs
) : ExpandRecordList<User>()

val tasks = client.records.getList<Task>(
    collection = "tasks",
    expand = Expand("assignee", "watchers")
)

tasks.items.forEach { task ->
    val assignee = task.expand?.get("assignee")
    val watchers = task.expand?.get("watchers") as? List<User>
    
    println("Task: ${task.title}")
    println("Assignee: ${assignee?.name}")
    println("Watchers: ${watchers?.joinToString { it.name }}")
}
```

## Field Selection

### Basic Field Selection

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields

// Select specific fields
val fields = ShowFields("id", "title", "status")

// Select all fields (default)
val fields = ShowFields("*")
```

### Field Selection with Relations

```kotlin
// Select fields from related records
val fields = ShowFields(
    "id", "title", "status",
    "author.name", "author.email",
    "category.name"
)

// Select all fields from main record and specific fields from relations
val fields = ShowFields(
    "*",
    "author.name",
    "author.email",
    "category.*"
)
```

### Field Selection Examples

```kotlin
// Minimal data for list view
val posts = client.records.getList<Post>(
    collection = "posts",
    fields = ShowFields("id", "title", "created", "author.name"),
    expand = Expand("author")
)

// Full data for detail view
val post = client.records.getOne<Post>(
    collection = "posts",
    id = "post_id",
    fields = ShowFields("*", "author.*", "category.*", "tags.*"),
    expand = Expand("author", "category", "tags")
)
```

## Combining Query Components

### Complete Query Example

```kotlin
val tasks = client.records.getList<Task>(
    collection = "tasks",
    page = 1,
    perPage = 20,
    filter = Filter("""
        status in ('active', 'in_progress') &&
        priority = 'high' &&
        assignee != null &&
        due_date >= '2024-01-01'
    """.trimIndent()),
    sort = Sort("-priority", "-created"),
    expand = Expand("assignee", "project", "tags"),
    fields = ShowFields(
        "id", "title", "status", "priority", "due_date",
        "assignee.name", "assignee.email",
        "project.name",
        "tags.name"
    )
)
```

### Dynamic Query Building

```kotlin
class TaskQuery {
    private var filterExpressions = mutableListOf<String>()
    private var sortFields = mutableListOf<String>()
    private var expandFields = mutableListOf<String>()
    private var showFields = mutableListOf<String>()
    
    fun filterByStatus(status: String): TaskQuery {
        filterExpressions.add("status = '$status'")
        return this
    }
    
    fun filterByPriority(priority: String): TaskQuery {
        filterExpressions.add("priority = '$priority'")
        return this
    }
    
    fun filterByAssignee(assigneeId: String): TaskQuery {
        filterExpressions.add("assignee = '$assigneeId'")
        return this
    }
    
    fun sortByCreated(descending: Boolean = false): TaskQuery {
        sortFields.add(if (descending) "-created" else "created")
        return this
    }
    
    fun sortByPriority(descending: Boolean = true): TaskQuery {
        sortFields.add(if (descending) "-priority" else "priority")
        return this
    }
    
    fun expandAssignee(): TaskQuery {
        expandFields.add("assignee")
        return this
    }
    
    fun expandProject(): TaskQuery {
        expandFields.add("project")
        return this
    }
    
    fun selectFields(vararg fields: String): TaskQuery {
        showFields.addAll(fields)
        return this
    }
    
    suspend fun execute(client: PocketbaseClient): ListResult<Task> {
        return client.records.getList(
            collection = "tasks",
            filter = Filter(filterExpressions.joinToString(" && ")),
            sort = Sort(*sortFields.toTypedArray()),
            expand = Expand(*expandFields.toTypedArray()),
            fields = ShowFields(*showFields.toTypedArray())
        )
    }
}

// Usage
val results = TaskQuery()
    .filterByStatus("active")
    .filterByPriority("high")
    .sortByPriority()
    .sortByCreated(descending = true)
    .expandAssignee()
    .expandProject()
    .selectFields("id", "title", "status", "assignee.name", "project.name")
    .execute(client)
```

## Performance Tips

1. **Use Field Selection**: Only select fields you need to reduce bandwidth
2. **Limit Expansion**: Only expand relations you actually use
3. **Efficient Filtering**: Use indexed fields for filtering when possible
4. **Pagination**: Use appropriate page sizes for large datasets
5. **Sorting**: Be mindful of sorting large datasets

## Common Patterns

### Search Functionality

```kotlin
fun searchTasks(query: String): Filter {
    return Filter("""
        title ~ '$query' ||
        description ~ '$query' ||
        assignee.name ~ '$query' ||
        project.name ~ '$query'
    """.trimIndent())
}
```

### Date Range Queries

```kotlin
fun filterByDateRange(start: String, end: String): Filter {
    return Filter("created >= '$start' && created <= '$end'")
}
```

### Status-based Queries

```kotlin
fun filterActiveItems(): Filter {
    return Filter("status in ('active', 'in_progress', 'pending')")
}

fun filterCompletedItems(): Filter {
    return Filter("status = 'completed' && completed_at != null")
}
```

### User-specific Queries

```kotlin
fun filterByCurrentUser(userId: String): Filter {
    return Filter("""
        assignee = '$userId' ||
        created_by = '$userId' ||
        watchers contains '$userId'
    """.trimIndent())
}
```