package data.manga

data class MangaAttributes(
    val contentRating: String?,
    val createdAt: String?,
    val description: Description,
    val lastChapter: String?,
    val lastVolume: String?,
    val originalLanguage: String?,
    val publicationDemographic: String?,
    val state: String?,
    val status: String?,
    val title: Title,
    val updatedAt: String?,
    val version: Int?,
    val year: Any?
)