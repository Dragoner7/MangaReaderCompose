package data.mangaFeed

data class MangaFeed(
    val `data`: List<ChapterDto>,
    val limit: Int,
    val offset: Int,
    val response: String,
    val result: String,
    val total: Int
)