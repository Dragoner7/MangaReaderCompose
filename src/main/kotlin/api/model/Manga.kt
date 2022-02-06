package api.model

class Manga(val id : String, val name : String, val description : String) {
    val chapters : List<Chapter> by lazy {
        MangaDex.getMangaChapters(this)
    }
}