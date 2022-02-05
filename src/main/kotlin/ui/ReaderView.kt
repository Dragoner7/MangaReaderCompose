package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Chapter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.skia.Image

/*
import javafx.scene.image.Image
import model.Chapter
import model.Manga
import tornadofx.*

class ReaderView : View("Manga reader") {
    private lateinit var manga : Manga
    private lateinit var currentChapter : Chapter
    private var currentPageIndex : Int = 0

    private var pageView = imageview()
    private var titleLabel = label { }

    private val cache : MutableList<Image> = mutableListOf()

    private var toolbar = borderpane {
        left = button("Back to manga"){
            action {
                backToInfoView()
            }
        }
        center = titleLabel
        right = hbox{
            button("Previous"){
                action {
                    flipBack()
                }
            }
            button("Next") {
                action {
                    flipForward()
                }
            }
        }
    }

    private fun backToInfoView() {
        replaceWith(MangaInfoView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
    }

    override val root = borderpane{
        top = toolbar
        center = pageView

        pageView.fitHeightProperty().bind(heightProperty() - toolbar.heightProperty())
        pageView.fitWidthProperty().bind(widthProperty())
        pageView.isPreserveRatio = true

        shortcut("Left"){
            flipBack()
        }
        shortcut("Right"){
            flipForward()
        }
    }

    private fun flipBack(){
        val newIndex = currentPageIndex - 1
        if(isNewPageValid(newIndex)){
            flipPage(newIndex)
        }
    }

    private fun flipForward(){
        val newIndex = currentPageIndex + 1
        if(isNewPageValid(newIndex)){
            flipPage(newIndex)
        }
    }

    private fun isNewPageValid(newIndex : Int) = newIndex >= 0 && newIndex < currentChapter.urls.size

    private fun flipPage(pageNumber : Int){
        currentPageIndex = pageNumber
        loadPage()
    }

    private fun loadPage(){
        val pageNumber = currentPageIndex
        pageView.image = cache[pageNumber]
    }

    fun onMangaLoad(manga : Manga){
        this.manga = manga
    }


    fun onChapterLoad(chapter : Chapter){
        this.currentChapter = chapter
        currentPageIndex = 0
        pageView.image = null
        titleLabel.text = "${manga.name} - $chapter"
        cache()
        loadPage()
    }

    private fun cache(){
        for(i in 0 until currentChapter.urls.size){
            cache.add(i, Image(currentChapter.urls[i], true))
        }
    }

}*/

@Composable
fun PageViewImage(readerState: ReaderState) {
    val image: ImageBitmap? by produceState<ImageBitmap?>(null, readerState) {
        value = withContext(Dispatchers.IO) {
            try {
                val imageRequest = Request.Builder().url(readerState.getCurrentPageUrl()).build()
                val client = OkHttpClient.Builder().build()
                val response = client.newCall(imageRequest).execute().body()?.bytes()
                Image.makeFromEncoded(response).toComposeImageBitmap()

            } catch (e : Exception){
                e.printStackTrace()
                null
            }
        }
    }

    if(image != null){
        Image(
            painter = BitmapPainter(image!!),
            contentDescription = null,
        )
    }
}

@Composable
@Preview
fun ReaderView(chapter: Chapter) {
    var readerState by remember { mutableStateOf(ReaderState(chapter, 0)) }
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ){
                PageViewImage(readerState)
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(onClick = {
                    readerState = readerState.flip(-1)
                }) {
                    Text("Previous")
                }
                Button(onClick = {
                    readerState = readerState.flip(1)
                }) {
                    Text("Next")
                }
            }
        }
    }
}
