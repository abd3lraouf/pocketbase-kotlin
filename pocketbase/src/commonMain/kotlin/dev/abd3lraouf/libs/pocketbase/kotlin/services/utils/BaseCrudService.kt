package dev.abd3lraouf.libs.pocketbase.kotlin.services.utils

import dev.abd3lraouf.libs.pocketbase.kotlin.FileUpload
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketKtInternal
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseException
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ExpandRelations
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Filter
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.SortFields
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.BaseModel
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.ListResult
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.http.path
import kotlinx.serialization.json.JsonPrimitive

public abstract class BaseCrudService<T : BaseModel>(
    client: PocketbaseClient,
) : BaseService(client) {
    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _getFullList(
        path: String,
        batch: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): List<T> {
        val result = mutableListOf<T>()
        var page = 1
        while (true) {
            val list =
                _getList<T>(
                    path,
                    page,
                    batch,
                    sortBy,
                    filterBy,
                    expandRelations,
                    showFields,
                    skipTotal,
                )
            val items = list.items.toMutableList()
            result.addAll(items)
            if (list.perPage != items.size) return result
            page += 1
        }
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _getList(
        path: String,
        page: Int,
        perPage: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): ListResult<T> {
        val response =
            client.httpClient.get {
                url {
                    path(path)
                    sortBy.addTo(parameters)
                    filterBy.addTo(parameters)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                    parameters.append("page", page.toString())
                    parameters.append("perPage", perPage.toString())
                    if (skipTotal) parameters.append("skipTotal", "1")
                }
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _getOne(
        path: String,
        id: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T {
        val response =
            client.httpClient.get {
                url {
                    path(path, id)
                    contentType(ContentType.Application.Json)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                }
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _create(
        path: String,
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T {
        val response =
            client.httpClient.post {
                url {
                    path(path)
                    contentType(ContentType.Application.Json)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                }
                setBody(body)
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _update(
        path: String,
        id: String,
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T {
        val response =
            client.httpClient.patch {
                url {
                    path(path, id)
                    contentType(ContentType.Application.Json)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                }
                setBody(body)
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    @PocketKtInternal
    public suspend inline fun _delete(
        path: String,
        id: String,
    ): Boolean {
        val response =
            client.httpClient.delete {
                url {
                    path(path, id)
                }
            }
        PocketbaseException.handle(response)
        return true
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _create(
        path: String,
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T {
        val response =
            client.httpClient.post {
                url {
                    path(path)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                }

                setBody(
                    MultiPartFormDataContent(
                        formData {
                            for (file in files) {
                                append(
                                    file.field,
                                    file.file ?: ByteArray(0),
                                    headers =
                                        if (file.file != null) {
                                            headersOf(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${file.fileName}\"",
                                            )
                                        } else {
                                            headersOf()
                                        },
                                )
                            }
                            body.forEach { (key, value) ->
                                append(key, value.content)
                            }
                        },
                    ),
                )
            }
        PocketbaseException.handle(response)
        return response.body()
    }

    @PocketKtInternal
    public suspend inline fun <reified T : BaseModel> _update(
        path: String,
        id: String,
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T {
        val response =
            client.httpClient.patch {
                url {
                    path(path, id)
                    expandRelations.addTo(parameters)
                    showFields.addTo(parameters)
                }

                setBody(
                    MultiPartFormDataContent(
                        formData {
                            for (file in files) {
                                append(
                                    file.field,
                                    file.file ?: ByteArray(0),
                                    headers =
                                        if (file.file != null) {
                                            headersOf(
                                                HttpHeaders.ContentDisposition,
                                                "filename=\"${file.fileName}\"",
                                            )
                                        } else {
                                            headersOf()
                                        },
                                )
                            }
                            body.forEach { (key, value) ->
                                append(key, value.content)
                            }
                        },
                    ),
                )
            }
        PocketbaseException.handle(response)
        return response.body()
    }
}
