# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform library providing a client SDK for [Pocketbase](https://pocketbase.io), a backend-as-a-service solution. The library targets Android and iOS platforms with a shared common codebase.

## Key Commands

### Build & Test
- `./gradlew build` - Build the entire project
- `./gradlew test` - Run unit tests for all platforms
- `./gradlew allTests` - Run tests for all targets with aggregated report
- `./gradlew check` - Run all checks including lint and tests
- `./gradlew clean` - Clean build artifacts

### Platform-Specific Testing
- `./gradlew testDebugUnitTest` - Run Android unit tests (debug)
- `./gradlew iosSimulatorArm64Test` - Run iOS simulator tests (ARM64)
- `./gradlew iosX64Test` - Run iOS simulator tests (x64)

### Publishing
- `./gradlew publishToMavenLocal` - Publish to local Maven repository
- `./gradlew publishToMavenCentral` - Publish to Maven Central (requires credentials)

## Architecture

### Module Structure
- `pocketbase/` - Main library module containing the SDK implementation
  - `src/commonMain/` - Shared Kotlin code for all platforms
  - `src/commonTest/` - Shared test code
  - `src/cioMain/` - CIO-specific implementation (HTTP client engine)

### Core Components

#### Client Entry Point
- `PocketbaseClient` - Main client class that provides access to all services
- Located in `pocketbase/src/commonMain/kotlin/dev/abd3lraouf/libs/pocketbase/kotlin/PocketbaseClient.kt`

#### Services Architecture
Services follow a hierarchical pattern:
- `BaseService` - Base class for all services
- `BaseCrudService` - Base for CRUD operations
- `CrudService` - Full CRUD operations
- `SubCrudService` - CRUD operations for sub-resources
- `AuthService` - Authentication-related operations

#### Available Services
- `RecordsService` - CRUD operations for records and collection auth
- `CollectionService` - Collection management
- `BackupsService` - Backup operations
- `BatchService` - Batch operations
- `HealthService` - Health checks
- `LogService` - Log management
- `RealtimeService` - Real-time updates via Server-Sent Events
- `SettingsService` - Server settings
- `FilesService` - File management
- `CronsService` - Cron job management

#### Models
- `BaseModel` - Base class for all models
- `TimestampedModel` - Models with timestamps
- `Record` - Generic record model
- `AuthRecord` - Authenticated record model
- `Collection` - Collection schema model
- Custom serialization for Pocketbase-specific types (InstantPocketbase, DurationPocketbase, GeoPoint)

#### Data Store
- `BaseAuthStore` - Manages authentication state and tokens

#### DSL Builders
- Query builders for filters, sorting, field selection, and expansion
- `BatchRequestBuilder` - DSL for batch operations
- `Login` - DSL for authentication

### Dependencies
- **Ktor** - HTTP client and content negotiation
- **Kotlinx Serialization** - JSON serialization
- **Kotlinx Coroutines** - Asynchronous operations
- **Kotlinx DateTime** - Date/time handling

### Platform Configuration
- **Android**: Minimum SDK 21, Compile SDK 36, JVM target 11
- **iOS**: Supports x64, ARM64, and Simulator ARM64
- **Common**: Uses CIO HTTP engine for all platforms

## Development Notes

### Explicit API Mode
The project uses `explicitApi()` which requires all public APIs to have explicit visibility modifiers and return types.

### Multiplatform Setup
- Uses Kotlin Multiplatform with custom source set dependencies
- CIO engine is used as the HTTP client engine for all platforms
- Tests are structured to run on all target platforms

### Package Structure
All code is organized under `dev.abd3lraouf.libs.pocketbase.kotlin` with logical subpackages for models, services, stores, and DSL builders.