package model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import data.MangaDexService
import data.cover.CoverDto
import data.cover.CoverRequest
import data.manga.MangaDto
import data.mangaFeed.AtHomeDto
import data.mangaFeed.ChapterDto
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object MangaDex {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory()).build()
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.mangadex.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private var service: MangaDexService = retrofit.create(MangaDexService::class.java)

    fun getManga(id: String): Manga? {
        try {
            val mangaRequest = service.getMangaById(id).execute().body()!!
            return Manga(mangaRequest.data.id, findName(mangaRequest.data), mangaRequest.data.attributes.description.en)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getMangaChapters(manga : Manga) : List<Chapter>{
        val result : MutableList<Chapter> = mutableListOf()
        for(chapterDto in manga.retrieveChapterDtos()){
            result.add(chapterDto.toChapter())
        }
        return result
    }

    fun getMangaByTitle(title : String): List<Manga>{
        val mangaList: MutableList<Manga> = mutableListOf()
        try {
            val result = service.getMangaByTitle(title).execute().body()!!

            for(mangaDto in result.data){
                mangaList.add(Manga(mangaDto.id, findName(mangaDto), mangaDto.attributes.description.en))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mangaList
    }

    private fun findName(mangaDto: MangaDto) : String = mangaDto.attributes.title.en ?: mangaDto.attributes.title.ja ?: "Unknown Title"

    fun getCovers(mangaList : List<Manga>) : Map<Manga, MutableList<Cover>>{
        val rqMap: MutableMap<String, String> = mangaListToQueryParameters(mangaList)
        val coverFilenameMap : MutableMap<Manga, MutableList<Cover>> = mutableMapOf()

        try {
            repeatUntilTotal {
                val response : CoverRequest = service.getCovers(it, rqMap).execute().body()!!
                for(coverDto in response.data){
                    val cover = coverDto.toCover()
                    val manga = mangaList.first { e -> e.id == cover.mangaId }
                    coverFilenameMap.putIfAbsent(manga, mutableListOf(cover))?.add(cover)
                }
                Pair(response.total, response.limit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return coverFilenameMap
    }

    private fun mangaListToQueryParameters(mangaList: List<Manga>): MutableMap<String, String> {
        val rqMap: MutableMap<String, String> = mutableMapOf()
        var index = 0
        for (manga in mangaList) {
            rqMap["manga[$index]"] = manga.id
            index += 1
        }
        return rqMap
    }

    private fun CoverDto.toCover() : Cover{
        val volumeNo = this.attributes.volume?.let { try{it.toInt() }catch (e : NumberFormatException){0}}
        return Cover(this.relationships.first { it.type == "manga" }.id, volumeNo, this.attributes.fileName)
    }

    private fun ChapterDto.toChapter() : Chapter{
        val chapter = Chapter(this.id,this.attributes.chapter?.toDouble() ?: 0.0, this.attributes.translatedLanguage ?: "unknown")
        chapter.also {
            val title = this.attributes.title
            if(title != null){
                it.title = title
            }
        }
        return chapter
    }

    private fun Manga.retrieveChapterDtos() : List<ChapterDto>{
        val chapters : MutableList<ChapterDto> = mutableListOf()
        try {
            repeatUntilTotal {
                val mangaFeed = service.getMangaChaptersAscendingWithOffset(this.id, it).execute().body()!!
                chapters.addAll(mangaFeed.data)
                Pair(mangaFeed.total, mangaFeed.limit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chapters
    }

    fun getChapterUrls(chapterId : String) : List<String>{
        return try {
            val baseUrl = service.getAtHomeServerForChapter(chapterId).execute().body()!!
            toChapterUrls(baseUrl)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toChapterUrls(baseUrl : AtHomeDto) : List<String>{
        val urls : MutableList<String> = mutableListOf()
        for(file : String in baseUrl.chapter.data)
            urls.add(baseUrl.baseUrl + "/data/" + baseUrl.chapter.hash + "/" + file)
        return urls
    }

    //Repeats function calls until total is reached.
    //The total value is the first value of the pair, the increments are the second
    private inline fun repeatUntilTotal(function : (Int)->Pair<Int, Int>){
        var offset = 0
        var total: Int
        do {
            val pair = function(offset)
            total = pair.first
            offset += pair.second
        } while (offset < total)
    }
}