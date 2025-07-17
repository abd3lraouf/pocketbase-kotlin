package dev.abd3lraouf.libs.pocketbase.kotlin.services

import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseException
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields
import dev.abd3lraouf.libs.pocketbase.kotlin.services.utils.BaseService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.Serializable

public class HealthService(
    client: PocketbaseClient,
) : BaseService(client) {
    @Serializable
    public data class HealthResponses(
        val code: Int,
        val message: String,
    )

    /** Returns the health status of the server. */
    public suspend fun healthCheck(fields: ShowFields = ShowFields()): HealthResponses {
        val response =
            client.httpClient.get {
                url {
                    path("api", "health")
                    fields.addTo(parameters)
                }
                contentType(ContentType.Application.Json)
            }
        PocketbaseException.handle(response)
        return response.body()
    }
}
