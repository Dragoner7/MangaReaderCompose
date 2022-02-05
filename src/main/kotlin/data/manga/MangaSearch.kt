package data.manga

data class MangaSearch(
    val `data`: List<MangaDto>,
    val response: String,
    val result: String
)