package repository.holder

data class DownloadItem(
    val path: String,
    val type: FileType,
    val isSuccess: Boolean
)

enum class FileType {
    Image, File
}