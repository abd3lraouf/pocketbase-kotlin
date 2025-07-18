package dev.abd3lraouf.libs.pocketbase.kotlin.models

import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.BaseModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
/**
 * A Pocketbase record retried from an API call.
 *
 * @property collectionId the ID of the record's [Collection].
 * @property collectionName the name of the record's [Collection].
 */
public open class Record(
    @Transient private val recordId: String? = null,
) : BaseModel(recordId) {
    public val collectionId: String? = null
    public val collectionName: String? = null

    override fun toString(): String = "Record(collectionId=$collectionId, collectionName=$collectionName)"
}
