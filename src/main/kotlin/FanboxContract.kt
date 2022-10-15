import io.ktor.client.statement.*
import repository.holder.Creator
import repository.holder.CreatorItem
import repository.holder.PostInfo
import java.io.File

interface FanboxContract {
    interface Interactor {
        suspend fun getCreator(): Creator?
        suspend fun getCreatorPaginates(): List<String>
        suspend fun getCreatorItems(maxPublishedTime: String, maxId: String, limit: String): List<CreatorItem>
        suspend fun getPostInfo(postId: String): PostInfo?
        suspend fun downloadItem(url: String, file: File): Boolean
    }

    interface ApiClient {
        suspend fun get(dir: String, parameters: Map<String, String> = emptyMap()): HttpResponse
        suspend fun download(url: String): HttpResponse
    }

    companion object {
        const val BASE_URL = "https://api.fanbox.cc/"
    }
}