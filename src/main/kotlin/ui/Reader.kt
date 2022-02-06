package ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import api.model.Chapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

data class Page(val number : Int, val url : String, var isDownloading : Boolean, var img : ImageBitmap?){
    val mutex = Mutex()
}

class Reader(chapter: Chapter, var onStateChange : (ReaderState)->Unit) {
    private val PRELOAD_PAGES = 3
    private var currentPageNumber = 0
    private var numOfPages : Int = chapter.urls.size
    private var ready : Boolean = false

    private val okHttpClient = OkHttpClient()

    init {
        onStateChange(ReaderState(0, 0, ready, 0.0f, null))
        chapterPreload(0, PRELOAD_PAGES)
    }

    private val pages : List<Page> = chapter.urls.mapIndexed{i, url-> Page(i, url, false, null)}

    private suspend fun downloadFuturePages(offset : Int, num : Int, afterEachDownload: (Int)->Unit){
        for (i in offset until min(offset + num, numOfPages)) {
            val page = pages[i]
            try {
                page.mutex.withLock {
                    if(!page.isDownloading && page.img == null) {
                        pages[i].isDownloading = true
                        val request = Request.Builder().url(pages[i].url).build()
                        okHttpClient.newCall(request).execute().use {
                            pages[i].img = it.body()?.let { it1 -> loadImageBitmap(it1.byteStream()) }
                        }
                        pages[i].isDownloading = false
                        afterEachDownload(i)
                    }
                }
            } catch (e: InterruptedException) {
                return
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun chapterPreload(offset : Int, num : Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try{
                downloadFuturePages(offset, num){
                    val progress = it.toFloat() / num.toFloat()
                    onStateChange(ReaderState(0, 0, ready, progress, null))
                }
                ready = true
                onStateChange(ReaderState(currentPageNumber, numOfPages, ready, 1.0f, pages[currentPageNumber].img))
            } catch (e :Exception){
                e.printStackTrace()
            }
        }
    }

    fun notifyStateChange(readerState: ReaderState){
        if(readerState.currentPageNumber != currentPageNumber){
            currentPageNumber = readerState.currentPageNumber
            if(pages[currentPageNumber].img == null){
                ready = false
                onStateChange(ReaderState(readerState.currentPageNumber, numOfPages, ready, 0.0f, null))
                chapterPreload(readerState.currentPageNumber, PRELOAD_PAGES)
            }else{
                onStateChange(ReaderState(currentPageNumber, numOfPages, ready, 1.0f, pages[currentPageNumber].img))
                futurePreload()
            }
        }
    }

    fun futurePreload(){
        val end = currentPageNumber + PRELOAD_PAGES
        val start = pages.indexOfFirst { p->p.img == null }
        val difference = end - start
        if(difference > 0){
            chapterPreload(start, difference)
        }
    }

    fun dispose(){
        onStateChange = {}
        for(page in pages){
            okHttpClient.dispatcher().cancelAll()
        }
        okHttpClient.connectionPool().evictAll()
    }

}