package ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import api.model.Chapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.math.min

class Reader(val chapter: Chapter, val onStateChange : (ReaderState)->Unit) {
    private val PRELOAD_PAGES = 3
    private var currentPageNumber = 0
    private var numOfPages : Int = chapter.urls.size
    private var ready : Boolean = false
    init {
        onStateChange(ReaderState(0, 0, ready, 0.0f, null))
        chapterLoad(0, PRELOAD_PAGES)
    }

    private val downloadedPages = mutableListOf<ImageBitmap>()

    @OptIn(DelicateCoroutinesApi::class)
    fun chapterLoad(offset : Int, num : Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try{
                for (i in offset until min(offset + num, numOfPages)) {
                    val img = URL(chapter.urls[i]).openStream().buffered().use(::loadImageBitmap)
                    downloadedPages.add(img)
                    val progress = i.toFloat() / num.toFloat()
                    onStateChange(ReaderState(0, 0, ready, progress, null))
                }
                ready = true
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