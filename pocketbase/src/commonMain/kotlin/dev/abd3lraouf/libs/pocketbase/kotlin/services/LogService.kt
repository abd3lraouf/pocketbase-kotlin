package dev.abd3lraouf.libs.pocketbase.kotlin.services

import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseException
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Filter
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.SortFields
import dev.abd3lraouf.libs.pocketbase.kotlin.models.Log
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.InstantPocketbase
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.ListResult
import dev.abd3lraouf.libs.pocketbase.kotlin.services.utils.BaseService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.Serializable

public class LogService(
    client: PocketbaseClient,
) : BaseService(client) {
    @Serializable
    public data class HourlyStats(
        val total: Int,
        val date: InstantPocketbase,
    )

    /**
     * Returns a paginated log list.
     *
     * @param page The page (aka. offset) of the paginated list.
     * @param perPage The max returned request logs per page.
     */
    public suspend fun getList(
        page: Int = 1,
        perPage: Int = 30,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        fields: ShowFields = ShowFields(),
    ): ListResult<Log> {
        val params =
            mapOf(
                "page" to page.toString(),
                "perPage" to perPage.toString(),
            )
        val response =
            client.httpClient.get {
                url {
                    path("api", "logs")
                    params.forEach { parameters.append(it.key, it.value) }
                    filterBy.addTo(parameters)
                    sortBy.addTo(parameters)
                    fields.addTo(parameters)
                }
                contentType(ContentType.Application.Json)
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    /**
     * Returns a single log by its ID.
     *
     * @param id The id of the long you wish to retrieve.
     */
    public suspend fun getOne(
        id: String,
        fields: ShowFields = ShowFields(),
    ): Log {
        val response =
            client.httpClient.get {
                url {
                    path("api", "logs", id)
                    fields.addTo(parameters)
                }
                contentType(ContentType.Application.Json)
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    /** Returns hourly aggregated logs statistics. */
    public suspend fun getStats(
        filterBy: Filter = Filter(),
        fields: ShowFields = ShowFields(),
    ): List<HourlyStats> {
        val response =
            client.httpClient.get {
                url {
                    path("api", "logs", "stats")
                    filterBy.addTo(parameters)
                    fields.addTo(parameters)
                }
                contentType(ContentType.Application.Json)
            }
        PocketbaseException.handle(response)
        return response.body()
    }
}
