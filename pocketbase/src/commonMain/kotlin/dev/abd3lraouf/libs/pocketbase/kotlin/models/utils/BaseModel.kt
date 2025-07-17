package dev.abd3lraouf.libs.pocketbase.kotlin.models.utils

import kotlinx.serialization.Serializable

@Serializable
/**
 * The base class used for all [Pocketbase](https://pocketbase.io) models
 * used.
 *
 * @param id The unique ID of the model.
 */
public open class BaseModel(
    public open val id: String? = null,
)
