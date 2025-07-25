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
public abstract class SubCrudService<T : BaseModel>(
    client: PocketbaseClient,
) : BaseCrudService<T>(client) {
    /** The url path to the service's API */
    public abstract fun baseCrudPath(collectionId: String): String

    /**
     * Fetches all records in the collection at once
     *
     * @param sub The collection you wish to preform this action on
     * @param batch The amount of records you wish to fetch.
     */
    public suspend inline fun <reified T : BaseModel> getFullList(
        sub: String,
        batch: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): List<T> =
        _getFullList(
            baseCrudPath(sub),
            batch,
            sortBy,
            filterBy,
            expandRelations,
            showFields,
            skipTotal,
        )

    /**
     * Fetches a paged list of records
     *
     * @param sub The collection you wish to preform this action on
     * @param page The page number you wish to fetch.
     * @param perPage The amount of records you wish to have per-single-page
     */
    public suspend inline fun <reified T : BaseModel> getList(
        sub: String,
        page: Int,
        perPage: Int,
        sortBy: SortFields = SortFields(),
        filterBy: Filter = Filter(),
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
        skipTotal: Boolean = false,
    ): ListResult<T> =
        _getList(
            baseCrudPath(sub),
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
     *
     * @param sub The collection you wish to preform this action on
     * @param id ID of the record you wish to view.
     */
    public suspend inline fun <reified T : BaseModel> getOne(
        sub: String,
        id: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _getOne(baseCrudPath(sub), id, expandRelations, showFields)

    /**
     * Creates a new record and gets the record
     *
     * @param sub The collection you wish to preform this action on
     * @param body JSON data used to create the record in the form of a string
     */
    public suspend inline fun <reified T : BaseModel> create(
        sub: String,
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _create(baseCrudPath(sub), body, expandRelations, showFields)

    /**
     * Updates an existing records and gets it
     *
     * @param sub The collection you wish to preform this action on
     * @param id the id of the record to update
     * @param body JSON data used to update the record in the form of a string
     */
    public suspend inline fun <reified T : BaseModel> update(
        sub: String,
        id: String,
        body: String,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _update(baseCrudPath(sub), id, body, expandRelations, showFields)

    /**
     * Creates a new record and gets the record
     *
     * @param sub The collection you wish to preform this action on
     * @param body the key value data used to create the record
     * @param files the files you wish to upload
     */
    public suspend inline fun <reified T : BaseModel> create(
        sub: String,
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _create(baseCrudPath(sub), body, files, expandRelations, showFields)

    /**
     * Updates an existing records and gets it
     *
     * @param sub The collection you wish to preform this action on
     * @param id the id of the record to update
     * @param body the key value data used to create the record
     * @param files the files you wish to upload
     */
    public suspend inline fun <reified T : BaseModel> update(
        sub: String,
        id: String,
        body: Map<String, JsonPrimitive>,
        files: List<FileUpload>,
        expandRelations: ExpandRelations = ExpandRelations(),
        showFields: ShowFields = ShowFields(),
    ): T = _update(baseCrudPath(sub), id, body, files, expandRelations, showFields)

    /**
     * Deletes the specified record
     *
     * @param sub The collection you wish to preform this action on
     * @param id the id of the record you wish to delete
     */
    public suspend inline fun delete(
        sub: String,
        id: String,
    ): Boolean = _delete(baseCrudPath(sub), id)
}
