package repository.holder

data class CreatorItem(
    val id: String,
    val title: String,
    val publishedTime: String,
    val updateTime: String,
    val isLicked: Boolean,
    val lickCount: Int,
    val commentCount: Int,
)