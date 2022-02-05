package model

class Cover(val mangaId : String, val volume : Int?, filename : String) {
    companion object{
        private const val baseUrl = "https://uploads.mangadex.org/covers"
        private fun getUrl(mangaId : String, filename: String): String = "$baseUrl/$mangaId/$filename"
    }
    val url = getUrl(mangaId,filename)
}
