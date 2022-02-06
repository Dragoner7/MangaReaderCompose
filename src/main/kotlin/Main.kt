import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.model.Chapter
import api.model.Manga
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
            fun onStateChange(state: ReaderState){
                readerState = state
            }

            fun notifyStateChange(state: ReaderState){
                reader.notifyStateChange(state)
            }

            fun onChapterChange(chapter: Chapter){
                selectedChapter = chapter
                windowState = WindowState.VIEW
                reader = Reader(chapter, ::onStateChange)
            }

            fun onMangaChange(manga: Manga){
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
