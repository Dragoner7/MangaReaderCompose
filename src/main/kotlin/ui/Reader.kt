package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Chapter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.skia.Image
import java.net.URL

class Reader(val chapter: Chapter, val onStateChange : (ReaderState)->Unit) {
    private val PRELOAD_PAGES = 3
    private var currentPageNumber = 0
    private var numOfPages : Int = 0
    private var ready : Boolean = false
    init {
        chapterLoad(0, PRELOAD_PAGES)
    }

    private val downloadedPages = mutableListOf<ImageBitmap>()

    fun chapterLoad(offset : Int, num : Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try{
                for (i in offset until offset + num) {
                    val img = URL(chapter.urls[i]).openStream().buffered().use(::loadImageBitmap)
                    downloadedPages.add(img)
                    val progress = i.toFloat() / num.toFloat()
                    onStateChange(ReaderState(0, 0, ready, progress, null))
                }
                ready = true
                numOfPages = chapter.urls.size
                onStateChange(ReaderState(currentPageNumber, numOfPages, ready, 1.0f, downloadedPages[offset]))
            } catch (e :Exception){
                e.printStackTrace()
            }
        }
    }

    fun notifyStateChange(readerState: ReaderState){
        if(readerState.currentPageNumber != currentPageNumber){
            currentPageNumber = readerState.currentPageNumber
            if(readerState.currentPageNumber >= downloadedPages.size){
                ready = false
                onStateChange(ReaderState(readerState.currentPageNumber, numOfPages, ready, 0.0f, null))
                chapterLoad(readerState.currentPageNumber, PRELOAD_PAGES)
            }else{
                onStateChange(ReaderState(currentPageNumber, numOfPages, ready, 1.0f, downloadedPages[currentPageNumber]))
            }
        }
    }

}