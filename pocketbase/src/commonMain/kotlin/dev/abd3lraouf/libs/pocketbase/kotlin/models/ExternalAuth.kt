package dev.abd3lraouf.libs.pocketbase.kotlin.models

import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.TimestampedModel
import kotlinx.serialization.Serializable

@Serializable
/** A Pocketbase external authentication provider */
public open class ExternalAuth : TimestampedModel() {
    public val userId: String? = null
    public val collectionId: String? = null
    public val provider: String? = null
    public val providerId: String? = null

    override fun toString(): String = "ExternalAuth(userId=$userId, collectionId=$collectionId, provider=$provider, providerId=$providerId)"
}
