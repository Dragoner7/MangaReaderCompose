import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import ui.*

enum class WindowState{
    SEARCH,
    INFO,
    VIEW
}

fun main() {
    lateinit var reader: Reader
    application {
        //for some reason hybrid GPU Windows doesn't like Direct3D for Compose
        System.setProperty("skiko.renderApi", "OPENGL")
        Window(onCloseRequest = ::exitApplication) {
            var selectedManga: Manga? by remember { mutableStateOf(null) }
            var selectedChapter: Chapter? by remember { mutableStateOf(null) }
            var windowState: WindowState by remember { mutableStateOf(WindowState.SEARCH) }
            var readerState by remember { mutableStateOf(ReaderState(0, 0, false, 0.0f, null)) }
            fun onStateChange(state: ReaderState): Unit {
                readerState = state
            }

            fun notifyStateChange(state: ReaderState): Unit {
                reader.notifyStateChange(state)
            }

            fun onChapterChange(chapter: Chapter): Unit {
                selectedChapter = chapter
                windowState = WindowState.VIEW
                reader = Reader(chapter, ::onStateChange)
            }

            fun onMangaChange(manga: Manga): Unit {
                selectedManga = manga
                windowState = WindowState.INFO
            }

            when (windowState) {
                WindowState.SEARCH -> SearchView(::onMangaChange)
                WindowState.INFO -> MangaInfoView(selectedManga!!, ::onChapterChange)
                WindowState.VIEW -> ReaderView(readerState, ::notifyStateChange)
            }

        }
    }
}
