package dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query

import io.ktor.http.ParametersBuilder

/**
 * This specifies which fields in a [Record] are shown in the request's
 * response See
 * [this example](https://github.com/pocketbase/pocketbase/releases/tag/v0.16.0)
 * for an explanation on how to use [ShowFields]
 *
 * @param fields all of the fields you want to be shown in the record's
 *    response
 */
public class ShowFields(
    vararg fields: String,
) {
    private var fieldsToShow: String = fields.joinToString(separator = ",")

    /** s Adds the current [ShowFields] to a request's parameters */
    public fun addTo(params: ParametersBuilder) {
        if (fieldsToShow.isNotEmpty()) params.append("fields", this.fieldsToShow)
    }
}
