package api.data.cover

data class CoverDto(
    val attributes: CoverAttributes,
    val id: String,
    val type: String,
    val relationships: List<Relationship>
)