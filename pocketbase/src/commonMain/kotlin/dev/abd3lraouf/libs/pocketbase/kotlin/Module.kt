package dev.abd3lraouf.libs.pocketbase.kotlin

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

internal expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
