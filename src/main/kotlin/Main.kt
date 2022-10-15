import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.*
import repository.FanboxInteractor
import repository.holder.CreatorItem
import repository.holder.FileType
import repository.holder.PostInfo
import repository.holder.DownloadItem
import util.ArgumentParser
import util.Console
import java.io.File

fun main(args: Array<String>) {
    val argumentParser = ArgumentParser(args, "PixivFanboxからデータを一括でダウンロードするプログラムです").apply {
        addSingleArgument("-f", "--force-update", help = "以前ダウンロードしたコンテンツを上書きします")
        addSingleArgument("-p", "--pack-items", help = "ディレクトリ分けせに保存します")
        addSingleArgument("-t", "--sort-by-time", help = "ディレクトリ名を公開日にします")
        addValueArgument("-s", "--session-id", valueName = "SESSION ID", help = "FANBOXSESSID（FANBOXのセッションID）")
        addValueArgument("creator_id", valueName = "CREATOR-ID", help = "クリエイターID", isRequire = true)
    }

    val arguments = argumentParser.parse()
    val fanboxInteractor = FanboxInteractor(arguments["creator_id"]!!, arguments["-s"])

    val fanboxDir = getDir(getCurrentDir(), "FanboxCollector")
    val creatorDir = getDir(fanboxDir, arguments["creator_id"]!!)

    runBlocking {
        val creator = fanboxInteractor.getCreator()
        val paginates = fanboxInteractor.getCreatorPaginates()

        val creatorItems = mutableListOf<CreatorItem>()
        val postInfoItems = mutableListOf<PostInfo>()
        val downloaded = mutableListOf<DownloadItem>()

        println("投稿データ一覧を取得中...")

        for ((index, url) in paginates.withIndex()) {
            val params = Url(url).parameters.flattenEntries()
            val items = fanboxInteractor.getCreatorItems(
                maxPublishedTime = params.find { it.first == "maxPublishedDatetime" }?.second ?: "",
                maxId = params.find { it.first == "maxId" }?.second ?: "",
                limit = params.find { it.first == "limit" }?.second ?: "",
            )

            creatorItems.addAll(items)

            Console.clearLine()
            Console.printProgress(paginates.size - 1, index)

            print("[${creatorItems.size}件 取得済み]")
        }

        Console.spaceLine()

        println("投稿データ詳細を取得中...")

        for ((index, item) in creatorItems.withIndex()) {
            val postInfo = fanboxInteractor.getPostInfo(item.id)
            postInfo?.let { postInfoItems.add(it) }

            Console.clearLine()
            Console.printProgress(creatorItems.size - 1, index)

            print("[${postInfoItems.size}件 取得済み]")

            delay(50)
        }

        Console.spaceLine()

        println("ダウンロード中...")

        val maxItems = postInfoItems.sumOf { it.images.size + it.files.size }
        val startTime = System.currentTimeMillis()
        var downloadedCount = 0

        for (item in postInfoItems) {
            fun getItemFile(extension: String, index: Int): File {
                val name = "${item.title}${if(index == 0) "" else "-$index"}.${extension}"
                return when {
                    arguments.containsKey("-p") -> File(creatorDir, name)
                    arguments.containsKey("-t") -> File(getDir(creatorDir, item.publishedTime), name)
                    else                       -> File(getDir(creatorDir, item.title), name)
                }
            }

            for ((n, image) in item.images.withIndex()) {
                val imageFile = getItemFile(image.extension, n)
                downloaded.add(DownloadItem(imageFile.absolutePath, FileType.Image, fanboxInteractor.downloadItem(image.originalUrl, imageFile)))

                Console.clearLine()
                Console.printProgress(maxItems - 1, downloadedCount, startTime = startTime, hasETA = true)

                print("[${downloadedCount}件 取得済み]")

                downloadedCount++
                delay(50)
            }

            for ((n, file) in item.files.withIndex()) {
                val fileFile = getItemFile(file.extension, n)
                downloaded.add(DownloadItem(fileFile.absolutePath, FileType.File, fanboxInteractor.downloadItem(file.url, fileFile)))

                Console.clearLine()
                Console.printProgress(maxItems - 1, downloadedCount, startTime = startTime, hasETA = true)

                print("[${downloadedCount}件 取得済み]")

                downloadedCount++
                delay(50)
            }
        }

        Console.spaceLine()

        println("完了 [データ保存先：${creatorDir.absolutePath}]")
    }
}

private fun getCurrentDir(): File {
    return File(System.getProperty("user.dir"))
}

private fun getDir(parent: File, name: String): File {
    val file = File(parent, name)
    if(file.exists() && file.isDirectory) return file

    file.mkdir()
    return file
}