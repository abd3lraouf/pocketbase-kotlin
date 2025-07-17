package dev.abd3lraouf.libs.pocketbase.kotlin.services

import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseException
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.BatchRequestBuilder
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.BatchResponse
import dev.abd3lraouf.libs.pocketbase.kotlin.services.utils.BaseService
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path

public class BatchService(
    client: PocketbaseClient,
) : BaseService(client) {
    /**
     * Batch and transactional create/update/upsert/delete of multiple records
     * in a single request.
     *
     * Use the DSL functions to create a batch request. See
     * [readme](https://github.com/agrevster/pocketbase-kotlin?#caveats)
     * for examples.
     */
    public suspend fun send(setup: BatchRequestBuilder.() -> Unit): List<BatchResponse> {
        val builder = BatchRequestBuilder()
        builder.setup()

        val response =
            client.httpClient.post {
                url {
                    path("api", "batch")
                }
                setBody(MultiPartFormDataContent(builder.createBatchBody()))
            }
        PocketbaseException.handle(response)
        return response.body()
    }
}
