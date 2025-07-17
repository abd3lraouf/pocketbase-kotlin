# Basic CRUD Operations

This example demonstrates the basic Create, Read, Update, Delete operations using the Pocketbase Kotlin SDK.

## Setup

```kotlin
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.models.Record
import dev.abd3lraouf.libs.pocketbase.kotlin.models.AuthRecord
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.login
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

// Create client
val client = PocketbaseClient(
    baseUrl = {
        protocol = URLProtocol.HTTP
        host = "localhost"
        port = 8090
    }
)

// Authenticate
val authResponse = client.records.authWithPassword<AuthRecord>(
    collection = "users",
    email = "user@example.com",
    password = "password123"
)

client.login {
    token = authResponse.token
}
```

## Define Data Models

```kotlin
@Serializable
data class Task(
    val title: String,
    val description: String? = null,
    val completed: Boolean = false,
    val priority: String = "medium", // low, medium, high
    val assignee: String? = null,
    val dueDate: String? = null
) : Record()

@Serializable
data class User(
    val name: String,
    val email: String,
    val department: String? = null
) : Record()

@Serializable
data class Project(
    val name: String,
    val description: String? = null,
    val status: String = "active", // active, completed, archived
    val owner: String? = null
) : Record()
```

## Create Operations

### Create a Single Record

```kotlin
suspend fun createTask() {
    val newTask = Task(
        title = "Complete project documentation",
        description = "Write comprehensive documentation for the new feature",
        priority = "high",
        dueDate = "2024-02-15"
    )
    
    try {
        val createdTask = client.records.create<Task>(
            collection = "tasks",
            body = newTask
        )
        
        println("Task created with ID: ${createdTask.id}")
        println("Title: ${createdTask.title}")
        println("Created: ${createdTask.created}")
    } catch (e: PocketbaseException) {
        println("Failed to create task: ${e.message}")
    }
}
```

### Create Multiple Records

```kotlin
suspend fun createMultipleTasks() {
    val tasks = listOf(
        Task("Review code changes", priority = "high"),
        Task("Update dependencies", priority = "medium"),
        Task("Fix UI bugs", priority = "low"),
        Task("Write unit tests", priority = "high")
    )
    
    val createdTasks = mutableListOf<Task>()
    
    tasks.forEach { task ->
        try {
            val created = client.records.create<Task>(
                collection = "tasks",
                body = task
            )
            createdTasks.add(created)
            println("Created task: ${created.title}")
        } catch (e: PocketbaseException) {
            println("Failed to create task '${task.title}': ${e.message}")
        }
    }
    
    println("Successfully created ${createdTasks.size} tasks")
}
```

## Read Operations

### Get a Single Record

```kotlin
suspend fun getTask(taskId: String) {
    try {
        val task = client.records.getOne<Task>(
            collection = "tasks",
            id = taskId
        )
        
        println("Task: ${task.title}")
        println("Status: ${if (task.completed) "Completed" else "Pending"}")
        println("Priority: ${task.priority}")
    } catch (e: PocketbaseException) {
        println("Task not found: ${e.message}")
    }
}
```

### Get Multiple Records

```kotlin
suspend fun getAllTasks() {
    try {
        val result = client.records.getList<Task>(
            collection = "tasks",
            page = 1,
            perPage = 50
        )
        
        println("Found ${result.totalItems} tasks (showing ${result.items.size})")
        
        result.items.forEach { task ->
            println("- ${task.title} (${task.priority})")
        }
    } catch (e: PocketbaseException) {
        println("Failed to fetch tasks: ${e.message}")
    }
}
```

### Get Records with Filtering

```kotlin
suspend fun getHighPriorityTasks() {
    try {
        val result = client.records.getList<Task>(
            collection = "tasks",
            filter = Filter("priority = 'high' && completed = false"),
            sort = Sort("-created"),
            page = 1,
            perPage = 20
        )
        
        println("High priority tasks:")
        result.items.forEach { task ->
            println("- ${task.title} (due: ${task.dueDate ?: "No deadline"})")
        }
    } catch (e: PocketbaseException) {
        println("Failed to fetch high priority tasks: ${e.message}")
    }
}
```

### Get Records with Pagination

```kotlin
suspend fun getTasksWithPagination() {
    var currentPage = 1
    val perPage = 10
    
    do {
        try {
            val result = client.records.getList<Task>(
                collection = "tasks",
                page = currentPage,
                perPage = perPage,
                sort = Sort("-created")
            )
            
            println("Page $currentPage of ${result.totalPages}:")
            result.items.forEach { task ->
                println("  - ${task.title}")
            }
            
            currentPage++
        } catch (e: PocketbaseException) {
            println("Failed to fetch page $currentPage: ${e.message}")
            break
        }
    } while (currentPage <= result.totalPages)
}
```

## Update Operations

### Update a Single Record

```kotlin
suspend fun updateTask(taskId: String) {
    try {
        // First, get the current task
        val currentTask = client.records.getOne<Task>(
            collection = "tasks",
            id = taskId
        )
        
        // Update the task
        val updatedTask = currentTask.copy(
            completed = true,
            priority = "low"
        )
        
        val result = client.records.update<Task>(
            collection = "tasks",
            id = taskId,
            body = updatedTask
        )
        
        println("Task updated successfully")
        println("Title: ${result.title}")
        println("Completed: ${result.completed}")
        println("Updated: ${result.updated}")
    } catch (e: PocketbaseException) {
        println("Failed to update task: ${e.message}")
    }
}
```

### Update Multiple Records

```kotlin
suspend fun completeAllHighPriorityTasks() {
    try {
        // Get all high priority, incomplete tasks
        val result = client.records.getList<Task>(
            collection = "tasks",
            filter = Filter("priority = 'high' && completed = false"),
            perPage = 100
        )
        
        val updatedTasks = mutableListOf<Task>()
        
        result.items.forEach { task ->
            try {
                val updated = client.records.update<Task>(
                    collection = "tasks",
                    id = task.id,
                    body = task.copy(completed = true)
                )
                updatedTasks.add(updated)
                println("Completed task: ${updated.title}")
            } catch (e: PocketbaseException) {
                println("Failed to update task '${task.title}': ${e.message}")
            }
        }
        
        println("Successfully completed ${updatedTasks.size} high priority tasks")
    } catch (e: PocketbaseException) {
        println("Failed to fetch high priority tasks: ${e.message}")
    }
}
```

### Conditional Updates

```kotlin
suspend fun updateTaskIfNotCompleted(taskId: String, newTitle: String) {
    try {
        val currentTask = client.records.getOne<Task>(
            collection = "tasks",
            id = taskId
        )
        
        if (currentTask.completed) {
            println("Cannot update completed task")
            return
        }
        
        val updated = client.records.update<Task>(
            collection = "tasks",
            id = taskId,
            body = currentTask.copy(title = newTitle)
        )
        
        println("Task title updated to: ${updated.title}")
    } catch (e: PocketbaseException) {
        println("Failed to update task: ${e.message}")
    }
}
```

## Delete Operations

### Delete a Single Record

```kotlin
suspend fun deleteTask(taskId: String) {
    try {
        val deleted = client.records.delete(
            collection = "tasks",
            id = taskId
        )
        
        if (deleted) {
            println("Task deleted successfully")
        } else {
            println("Failed to delete task")
        }
    } catch (e: PocketbaseException) {
        println("Failed to delete task: ${e.message}")
    }
}
```

### Delete Multiple Records

```kotlin
suspend fun deleteCompletedTasks() {
    try {
        // Get all completed tasks
        val result = client.records.getList<Task>(
            collection = "tasks",
            filter = Filter("completed = true"),
            perPage = 100
        )
        
        var deletedCount = 0
        
        result.items.forEach { task ->
            try {
                val deleted = client.records.delete(
                    collection = "tasks",
                    id = task.id
                )
                if (deleted) {
                    deletedCount++
                    println("Deleted task: ${task.title}")
                }
            } catch (e: PocketbaseException) {
                println("Failed to delete task '${task.title}': ${e.message}")
            }
        }
        
        println("Successfully deleted $deletedCount completed tasks")
    } catch (e: PocketbaseException) {
        println("Failed to fetch completed tasks: ${e.message}")
    }
}
```

### Safe Delete with Confirmation

```kotlin
suspend fun safeDeleteTask(taskId: String) {
    try {
        // First, get the task to show details
        val task = client.records.getOne<Task>(
            collection = "tasks",
            id = taskId
        )
        
        println("Are you sure you want to delete this task?")
        println("Title: ${task.title}")
        println("Description: ${task.description}")
        println("Priority: ${task.priority}")
        
        // In a real app, you'd show a confirmation dialog
        val confirmed = true // getUserConfirmation()
        
        if (confirmed) {
            val deleted = client.records.delete(
                collection = "tasks",
                id = taskId
            )
            
            if (deleted) {
                println("Task '${task.title}' deleted successfully")
            }
        } else {
            println("Delete cancelled")
        }
    } catch (e: PocketbaseException) {
        println("Failed to delete task: ${e.message}")
    }
}
```

## Advanced CRUD Operations

### Batch Operations

```kotlin
suspend fun batchTaskOperations() {
    try {
        val results = client.batch.send {
            // Create new tasks
            create("tasks", Task("New task 1", priority = "high"))
            create("tasks", Task("New task 2", priority = "medium"))
            
            // Update existing task
            update("tasks", "existing_task_id", Task("Updated task title"))
            
            // Delete completed tasks
            delete("tasks", "completed_task_id")
        }
        
        println("Batch operation completed with ${results.size} operations")
    } catch (e: PocketbaseException) {
        println("Batch operation failed: ${e.message}")
    }
}
```

### Search Operations

```kotlin
suspend fun searchTasks(query: String) {
    try {
        val result = client.records.getList<Task>(
            collection = "tasks",
            filter = Filter("title ~ '$query' || description ~ '$query'"),
            sort = Sort("-created"),
            page = 1,
            perPage = 20
        )
        
        println("Found ${result.totalItems} tasks matching '$query':")
        result.items.forEach { task ->
            println("- ${task.title}")
        }
    } catch (e: PocketbaseException) {
        println("Search failed: ${e.message}")
    }
}
```

### Aggregation Operations

```kotlin
suspend fun getTaskStatistics() {
    try {
        val allTasks = client.records.getList<Task>(
            collection = "tasks",
            perPage = 1000 // Get all tasks
        )
        
        val total = allTasks.totalItems
        val completed = allTasks.items.count { it.completed }
        val pending = total - completed
        
        val priorityCount = allTasks.items.groupBy { it.priority }
            .mapValues { it.value.size }
        
        println("Task Statistics:")
        println("Total: $total")
        println("Completed: $completed")
        println("Pending: $pending")
        println("By Priority:")
        priorityCount.forEach { (priority, count) ->
            println("  $priority: $count")
        }
    } catch (e: PocketbaseException) {
        println("Failed to get statistics: ${e.message}")
    }
}
```

## Error Handling Best Practices

```kotlin
suspend fun robustTaskOperation(taskId: String) {
    try {
        val task = client.records.getOne<Task>(
            collection = "tasks",
            id = taskId
        )
        
        // Perform operation
        val updated = client.records.update<Task>(
            collection = "tasks",
            id = taskId,
            body = task.copy(completed = true)
        )
        
        println("Task completed: ${updated.title}")
        
    } catch (e: PocketbaseException) {
        when (e.statusCode) {
            404 -> println("Task not found")
            400 -> println("Invalid request: ${e.message}")
            401 -> println("Unauthorized - please login again")
            403 -> println("Forbidden - insufficient permissions")
            500 -> println("Server error - please try again later")
            else -> println("Unknown error: ${e.message}")
        }
    } catch (e: Exception) {
        println("Unexpected error: ${e.message}")
    }
}
```

## Running the Examples

```kotlin
suspend fun main() {
    // Create some tasks
    createTask()
    createMultipleTasks()
    
    // Read tasks
    getAllTasks()
    getHighPriorityTasks()
    
    // Update tasks
    updateTask("task_id")
    completeAllHighPriorityTasks()
    
    // Delete tasks
    deleteCompletedTasks()
    
    // Advanced operations
    searchTasks("documentation")
    getTaskStatistics()
}
```