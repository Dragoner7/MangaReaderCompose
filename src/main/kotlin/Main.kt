import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Chapter
import model.Manga
import model.MangaDex
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.skia.Image
import ui.MangaInfoView
import ui.ReaderState
import ui.ReaderView
import ui.SearchView

enum class WindowState{
    SEARCH,
    INFO,
    VIEW
}

fun main() = application {
    //for some reason hybrid GPU Windows doesn't like Direct3D for Compose
    System.setProperty("skiko.renderApi", "OPENGL")
    Window(onCloseRequest = ::exitApplication) {
        var selectedManga : Manga? by remember { mutableStateOf(null) }
        var selectedChapter : Chapter? by remember { mutableStateOf(null) }
        var windowState : WindowState by remember { mutableStateOf(WindowState.SEARCH) }
        var readerState : ReaderState by remember { mutableStateOf(ReaderState(0, 0,false, 0.0f)) }
        fun onStateChange(state: ReaderState) : Unit{
            readerState = state
        }
        fun onChapterChange(chapter: Chapter) : Unit{
            selectedChapter = chapter
            windowState = WindowState.VIEW
            chapterLoad(chapter, ::onStateChange)
        }
        fun onMangaChange(manga: Manga) : Unit{
            selectedManga = manga
            windowState = WindowState.INFO
        }

        when(windowState){
            WindowState.SEARCH -> SearchView(::onMangaChange)
            WindowState.INFO -> MangaInfoView(selectedManga!!, ::onChapterChange)
            WindowState.VIEW -> ReaderView(readerState, ::onStateChange)
            else -> {Text(text = "Not yet implemented")}
        }

    }
}

fun chapterLoad(chapter: Chapter, onStateChange : (ReaderState) -> Unit) {
    val downloadedPages = mutableListOf<ImageBitmap>()
    GlobalScope.launch(Dispatchers.IO) {
        var cnt = 0
        for (url in chapter.urls) {
            try {
                val imageRequest = Request.Builder().url(url).build()
                val client = OkHttpClient.Builder().build()
                val response = client.newCall(imageRequest).execute().body()?.bytes()
                downloadedPages.add(Image.makeFromEncoded(response).toComposeImageBitmap())
                val progress = downloadedPages.size.toFloat() / chapter.urls.size.toFloat()
                onStateChange(ReaderState(0, 0, false, progress))
                cnt +=1;
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onStateChange(ReaderState(0, chapter.urls.size, true, 1.0f).also { it.downloadedPages = downloadedPages })
    }
}

