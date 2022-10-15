package repository.holder

import kotlinx.serialization.Serializable

@Serializable
data class DownloadItem(
    val url: String,
    val path: String,
    val type: FileType,
    val isSuccess: Boolean,
)

enum class FileType {
    Image, File
}