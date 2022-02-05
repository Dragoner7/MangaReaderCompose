import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Chapter
import model.MangaDex
import ui.MangaInfoView
import ui.ReaderView

enum class WindowState{
    SEARCH,
    INFO,
    VIEW
}

fun main() = application {
    //for some reason hybrid GPU Windows doesn't like Direct3D for Compose
    System.setProperty("skiko.renderApi", "OPENGL")
    //Neon Genesis Evangelion - Chapter 1 in a foreign language, depends on which order it's in the JSON
    var manga = remember { MangaDex.getManga("aaedcbda-ea61-4e7b-8143-7a475f327fbf") }
    var chapter = remember { manga!!.chapters[0] }
    Window(onCloseRequest = ::exitApplication) {
        var selectedChapter : Chapter? by remember { mutableStateOf(null) }
        var windowState : WindowState by remember { mutableStateOf(WindowState.INFO) }
        fun onChapterChange(chapter: Chapter) : Unit{
            selectedChapter = chapter
            windowState = WindowState.VIEW
        }
        when(windowState){
            WindowState.INFO -> MangaInfoView(manga!!, ::onChapterChange)
            WindowState.VIEW -> ReaderView(selectedChapter!!)
            else -> {Text(text = "Not yet implemented")}
        }

    }
}

