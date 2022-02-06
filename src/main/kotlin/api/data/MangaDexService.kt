package api.data

import api.data.cover.CoverRequest
import api.data.manga.MangaRequest
import api.data.manga.MangaSearch
import api.data.mangaFeed.AtHomeDto
import api.data.mangaFeed.MangaFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MangaDexService {
    @GET("manga")
    fun getMangaByTitle(@Query("title") title : String) : Call<MangaSearch>

    @GET("manga/{id}")
    fun getMangaById(@Path("id") id : String) : Call<MangaRequest>

    @GET("manga/{id}/feed?order[chapter]=asc")
    fun getMangaChaptersAscendingWithOffset(@Path("id") id : String, @Query("offset") offset : Int) : Call<MangaFeed>

    fun getMangaChaptersAscending(id : String) : Call<MangaFeed> = getMangaChaptersAscendingWithOffset(id, 0)

    @GET("at-home/server/{chapterID}")
    fun getAtHomeServerForChapter(@Path("chapterID") chapterID : String) : Call<AtHomeDto>

    @GET("cover")
    fun getCovers(@Query("offset") offset: Int, @QueryMap mangaList : Map<String, String>) : Call<CoverRequest>
}