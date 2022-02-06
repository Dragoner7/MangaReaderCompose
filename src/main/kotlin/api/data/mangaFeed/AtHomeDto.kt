package api.data.mangaFeed

data class AtHomeDto(
    val baseUrl: String,
    val result: String,
    val chapter : AtHomeChapterAttributes
)