package repository.entity

import kotlinx.serialization.Serializable

@Serializable
data class CreatorPaginatesEntity(
    val body: List<String>
)