package dev.abd3lraouf.libs.pocketbase.kotlin.dsl

import dev.abd3lraouf.libs.pocketbase.kotlin.FileUpload
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketKtDSL
import io.ktor.client.request.forms.formData
import io.ktor.http.HttpHeaders
import io.ktor.http.content.PartData
import io.ktor.http.escapeIfNeeded
import io.ktor.http.headersOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class BatchRequest(
    val method: BatchRequestMethod,
    val url: String,
    @Transient val collectionId: String? = null,
    val body: JsonObject? = null,
    @Transient val files: List<FileUpload>? = null,
    val headers: Map<String, String>? = null,
) {
    enum class BatchRequestMethod {
        POST,
        PATCH,
        PUT,
        DELETE,
    }
}

@Serializable
public data class BatchResponse(
    val status: Int,
    val body: JsonObject? = null,
)

@Serializable
private data class BatchRequestList(
    val requests: List<BatchRequest>,
)

@PocketKtDSL
public class BatchRequestBuilder {
    private val requests = mutableListOf<BatchRequest>()

    public fun create(
        collectionId: String,
        recordJson: JsonObject? = null,
        files: List<FileUpload>? = null,
        headers: Map<String, String>? = null,
    ) {
        requests.add(
            BatchRequest(
                BatchRequest.BatchRequestMethod.POST,
                "/api/collections/$collectionId/records",
                collectionId,
                recordJson,
                files,
                headers,
            ),
        )
    }

    public fun update(
        collectionId: String,
        recordId: String,
        recordJson: JsonObject? = null,
        files: List<FileUpload>? = null,
        headers: Map<String, String>? = null,
    ) {
        requests.add(
            BatchRequest(
                BatchRequest.BatchRequestMethod.PATCH,
                "/api/collections/$collectionId/records/$recordId",
                collectionId,
                recordJson,
                files,
                headers,
            ),
        )
    }

    public fun upsert(
        collectionId: String,
        recordJson: JsonObject? = null,
        files: List<FileUpload>? = null,
        headers: Map<String, String>? = null,
    ) {
        requests.add(
            BatchRequest(
                BatchRequest.BatchRequestMethod.PUT,
                "/api/collections/$collectionId/records",
                collectionId,
                recordJson,
                files,
                headers,
            ),
        )
    }

    public fun delete(
        collectionId: String,
        recordId: String,
        recordJson: JsonObject? = null,
        files: List<FileUpload>? = null,
        headers: Map<String, String>? = null,
    ) {
        requests.add(
            BatchRequest(
                BatchRequest.BatchRequestMethod.DELETE,
                "/api/collections/$collectionId/records/$recordId",
                collectionId,
                recordJson,
                files,
                headers,
            ),
        )
    }

    internal fun createBatchBody(): List<PartData> =
        formData {
            append("@jsonPayload", Json.encodeToString(BatchRequestList(requests)))
            requests.forEachIndexed { index, request ->
                request.files?.forEach { file ->
                    append(
                        "requests.$index.${file.field}",
                        file.file!!,
                        headers =
                            headersOf(
                                HttpHeaders.ContentDisposition,
                                "filename=${file.fileName.escapeIfNeeded()}",
                            ),
                    )
                }
            }
        }
}
