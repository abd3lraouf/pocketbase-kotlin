package dev.abd3lraouf.libs.pocketbase.kotlin.services

import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseException
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields
import dev.abd3lraouf.libs.pocketbase.kotlin.models.CronJob
import dev.abd3lraouf.libs.pocketbase.kotlin.services.utils.BaseService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

public class CronsService(
    client: PocketbaseClient,
) : BaseService(client) {
    /** Returns list with all registered app level cron jobs. */
    public suspend fun list(fields: ShowFields = ShowFields()): List<CronJob> {
        val response =
            client.httpClient.get {
                url {
                    path("api", "crons")
                    fields.addTo(parameters)
                }
                contentType(ContentType.Application.Json)
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    /**
     * Triggers a single cron job by its id.
     *
     * @param jobId The identifier of the cron job to run.
     */
    public suspend fun run(jobId: String) {
        val response =
            client.httpClient.post {
                url {
                    path("api", "crons", jobId)
                }
            }
        PocketbaseException.handle(response)
    }
}
