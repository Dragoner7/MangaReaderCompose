package api.model

object CoverStorage {
    private val map : MutableMap<Manga, MutableList<Cover>> = mutableMapOf()

    fun getCover(manga : Manga) : List<Cover>{
        return map.getOrDefault(manga, retrieveCover(manga))
    }

    private fun retrieveCover(manga: Manga) = retrieveCovers(listOf(manga))[manga]!!

    private fun retrieveCovers(mangaList: List<Manga>) :  Map<Manga, List<Cover>>{
        val newCovers = MangaDex.getCovers(mangaList)
        map.putAll(newCovers)
        return newCovers
    }
    private fun Iterable<Cover>.firstCoverSearch() : Cover? {
        val findNullId = this.firstOrNull { e -> e.volume == null }
        if(findNullId != null){
            return findNullId
        }
        return this.sortedBy { e->e.volume }.firstOrNull()
    }
    fun getFirstCover(manga : Manga) = getCover(manga).firstCoverSearch()
    fun getFirstCovers(mangaList: List<Manga>) : Map<Manga, Cover>{
        val covers = retrieveCovers(mangaList)
        val result : MutableMap<Manga, Cover> = mutableMapOf()
        for(cover in covers){
             cover.value.firstCoverSearch()?.let { result[cover.key] = it }
        }
        return result
    }
}