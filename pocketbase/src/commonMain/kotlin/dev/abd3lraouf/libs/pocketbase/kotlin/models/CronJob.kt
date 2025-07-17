package dev.abd3lraouf.libs.pocketbase.kotlin.models

import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.BaseModel
import kotlinx.serialization.Serializable

@Serializable
/**
 * A Pocketbase app cron job used by the CronService.
 *
 * @param expression The cron expression used to designate how often the
 *    given job is run.
 */
public data class CronJob(
    val expression: String,
) : BaseModel() {
    override fun toString(): String = "CronJob(expression='$expression' id='$id')"
}
