package repository.holder

data class PostInfo(
    val id: String,
    val title: String,
    val feeRequired: Int,
    val publishedTime: String,
    val updatedTime: String,
    val images: List<Image>,
    val files: List<File>,
    val tags: List<String>,
    val excerpt: String,
    val isLicked: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val userId: String,
    val userName: String,
    val creatorId: String,
) {
    data class Image(
        val id: String,
        val extension: String,
        val originalUrl: String,
        val thumbnailUrl: String,
    )

    data class File(
        val id: String,
        val name: String,
        val extension: String,
        val size: Long,
        val url: String,
    )
}