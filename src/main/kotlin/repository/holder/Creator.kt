package repository.holder

data class Creator(
    val userId: String,
    val userName: String,
    val creatorId: String,
    val description: String,
    val hasAdultContent: Boolean,
    val isFollowed: Boolean,
    val isSupported: Boolean,
)