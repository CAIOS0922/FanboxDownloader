package repository

import FanboxContract
import client.FanboxApiClient
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import repository.entity.PostInfoEntity
import repository.holder.Creator
import repository.holder.CreatorItem
import repository.holder.PostInfo
import java.io.File

class FanboxInteractor(
    private val creatorId: String,
    private val sessionId: String?,
): FanboxContract.Interactor {

    private val apiClient = FanboxApiClient(sessionId)
    private val dataStore = FanboxDataStore(apiClient)

    override suspend fun getCreator(): Creator? = dataStore.fetchCreator(creatorId)?.let { entity ->
        Creator(
            userId = entity.body.user.userId,
            userName = entity.body.user.name,
            creatorId = entity.body.creatorId,
            description = entity.body.description,
            hasAdultContent = entity.body.hasAdultContent,
            isFollowed = entity.body.isFollowed,
            isSupported = entity.body.isSupported,
        )
    }

    override suspend fun getCreatorPaginates(): List<String> = dataStore.fetchCreatorPaginates(creatorId)?.body ?: emptyList()

    override suspend fun getCreatorItems(maxPublishedTime: String, maxId: String, limit: String): List<CreatorItem> = dataStore.fetchCreatorItems(
        creatorId = creatorId,
        maxPublishedTime = maxPublishedTime,
        maxId = maxId,
        limit = limit
    )?.let { entity ->
        entity.body.items.map { item ->
            CreatorItem(
                id = item.id,
                title = item.title,
                publishedTime = item.publishedDatetime,
                updateTime = item.updatedDatetime,
                isLicked = item.isLiked,
                lickCount = item.likeCount,
                commentCount = item.commentCount,
            )
        }
    } ?: emptyList()

    override suspend fun getPostInfo(postId: String): PostInfo? = dataStore.fetchPostInfo(postId)?.let { entity ->
        PostInfo(
            id = entity.body.id,
            title = entity.body.title,
            feeRequired = entity.body.feeRequired,
            publishedTime = entity.body.publishedDatetime,
            updatedTime = entity.body.updatedDatetime,
            images = ((entity.body.body?.imageMap?.map { it.value } ?: emptyList()) + (entity.body.body?.images ?: emptyList())).map { map ->
                PostInfo.Image(
                    id = map.id,
                    extension = map.extension,
                    originalUrl = map.originalUrl,
                    thumbnailUrl = map.thumbnailUrl,
                )
            },
            files = entity.body.body?.fileMap?.map { map ->
                PostInfo.File(
                    id = map.value.id,
                    name = map.value.name,
                    extension = map.value.extension,
                    size = map.value.size,
                    url = map.value.url,
                )
            } ?: emptyList(),
            tags = entity.body.tags,
            excerpt = entity.body.excerpt,
            isLicked = entity.body.isLiked,
            likeCount = entity.body.likeCount,
            commentCount = entity.body.commentCount,
            userId = entity.body.user.userId,
            userName = entity.body.user.name,
            creatorId = entity.body.creatorId,
        )
    }

    @OptIn(InternalAPI::class)
    override suspend fun downloadItem(url: String, file: File): Boolean {
        return try {
            val response = apiClient.download(url)

            if (response.status.isSuccess()) {
                response.content.copyAndClose(file.writeChannel())
                true
            } else {
                false
            }
        } catch (e: Throwable) {
            false
        }
    }
}