package api.data.cover

data class CoverAttributes(
    val createdAt: String,
    val description: String,
    val fileName: String,
    val updatedAt: String,
    val version: Int,
    val volume: String?
)