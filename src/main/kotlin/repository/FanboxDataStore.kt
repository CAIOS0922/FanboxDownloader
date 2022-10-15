package repository

import repository.entity.CreatorEntity
import repository.entity.CreatorItemsEntity
import repository.entity.CreatorPaginatesEntity
import repository.entity.PostInfoEntity
import util.parse

class FanboxDataStore(private val apiClient: FanboxContract.ApiClient) {

    suspend fun fetchCreator(creatorId: String): CreatorEntity? {
        return apiClient.get("creator.get", mapOf("creatorId" to creatorId)).parse()
    }

    suspend fun fetchCreatorPaginates(creatorId: String): CreatorPaginatesEntity? {
        return apiClient.get("post.paginateCreator", mapOf("creatorId" to creatorId)).parse()
    }

    suspend fun fetchCreatorItems(
        creatorId: String,
        maxPublishedTime: String,
        maxId: String,
        limit: String,
    ): CreatorItemsEntity? {
        return apiClient.get("post.listCreator", mapOf(
            "creatorId" to creatorId,
            "maxPublishedDatetime" to maxPublishedTime,
            "maxId" to maxId,
            "limit" to limit
        )).parse()
    }

    suspend fun fetchPostInfo(postId: String): PostInfoEntity? {
        return apiClient.get("post.info", mapOf("postId" to postId)).parse()
    }
}