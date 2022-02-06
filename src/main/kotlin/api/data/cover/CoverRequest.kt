package api.data.cover

data class CoverRequest(
    val `data`: List<CoverDto>,
    val limit: Int,
    val offset: Int,
    val response: String,
    val result: String,
    val total: Int
)