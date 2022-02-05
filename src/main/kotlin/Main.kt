import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Chapter
import model.Manga
import model.MangaDex
import ui.MangaInfoView
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
        fun onChapterChange(chapter: Chapter) : Unit{
            selectedChapter = chapter
            windowState = WindowState.VIEW
        }
        fun onMangaChange(manga: Manga) : Unit{
            selectedManga = manga
            windowState = WindowState.INFO
        }
        when(windowState){
            WindowState.SEARCH -> SearchView(::onMangaChange)
            WindowState.INFO -> MangaInfoView(selectedManga!!, ::onChapterChange)
            WindowState.VIEW -> ReaderView(selectedChapter!!)
            else -> {Text(text = "Not yet implemented")}
        }

    }
}

