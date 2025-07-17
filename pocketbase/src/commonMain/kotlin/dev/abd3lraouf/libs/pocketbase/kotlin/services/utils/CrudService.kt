@file:OptIn(PocketKtInternal::class)

package dev.abd3lraouf.libs.pocketbase.kotlin.services.utils

import dev.abd3lraouf.libs.pocketbase.kotlin.FileUpload
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketKtInternal
import dev.abd3lraouf.libs.pocketbase.kotlin.PocketbaseClient
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ExpandRelations
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.Filter
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.ShowFields
import dev.abd3lraouf.libs.pocketbase.kotlin.dsl.query.SortFields
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.BaseModel
import dev.abd3lraouf.libs.pocketbase.kotlin.models.utils.ListResult
import kotlinx.serialization.json.JsonPrimitive

@OptIn(PocketKtInternal::class)
public abstract class CrudService<T : BaseModel>(
    client: PocketbaseClient,
) : BaseCrudService<T>(client) {
    /**
     * The url path to the service's API
     */
    public abstract val baseCrudPath: String

    /**
     * Fetches all records in the collection at once
     * @param [batch] The amount of records you wish to fetch.
     */
    public suspend inline fun <reified T : BaseModel> getFullList(
        batch: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): List<T> =
        _getFullList(
            baseCrudPath,
            batch,
            sortBy,
            filterBy,
            expandRelations,
            showFields,
            skipTotal,
        )

    /**
     * Fetches a paged list of records
     * @param [page] The page number you wish to fetch.
     * @param [perPage] The amount of records you wish to have per-single-page
     */
    public suspend inline fun <reified T : BaseModel> getList(
        page: Int,
        perPage: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): ListResult<T> =
        _getList(
            baseCrudPath,
            page,
            perPage,
            sortBy,
            filterBy,
            expandRelations,
            showFields,
            skipTotal,
        )

    /**
     * Fetches a single record
     * @param [id] ID of the record you wish to view.
     */
    public suspend inline fun <reified T : BaseModel> getOne(
        id: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _getOne(baseCrudPath, id, expandRelations, showFields)

    /**
     * Creates a new record and gets the record
     * @param [body] JSON data used to create the record in the form of a string
     */
    public suspend inline fun <reified T : BaseModel> create(
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _create(baseCrudPath, body, expandRelations, showFields)

    /**
     * Updates an existing records and gets it
     * @param [id] the id of the record to update
     * @param [body] JSON data used to update the record in the form of a string
     */
    public suspend inline fun <reified T : BaseModel> update(
        id: String,
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _update(baseCrudPath, id, body, expandRelations, showFields)

    /**
     * Creates a new record and gets the record
     * @param [body] the key value data used to create the record
     * @param [files] the files you wish to upload
     */
    public suspend inline fun <reified T : BaseModel> create(
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _create(baseCrudPath, body, files, expandRelations, showFields)

    /**
     * Updates an existing records and gets it
     * @param [id] the id of the record to update
     * @param [body] the key value data used to create the record
     * @param [files] the files you wish to upload
     */
    public suspend inline fun <reified T : BaseModel> update(
        id: String,
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _update(baseCrudPath, id, body, files, expandRelations, showFields)

    /***
     * Deletes the specified record
     * @param [id] the id of the record you wish to delete
     */
    public suspend inline fun delete(id: String): Boolean = _delete(baseCrudPath, id)
}
