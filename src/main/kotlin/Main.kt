import androidx.compose.animation.*
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

@OptIn(ExperimentalAnimationApi::class)
fun main() {
    application {
        //for some reason hybrid GPU Windows doesn't like Direct3D for Compose
        System.setProperty("skiko.renderApi", "OPENGL")
        Window(onCloseRequest = ::exitApplication) {
            var reader: Reader? by remember { mutableStateOf(null) }
            var selectedManga: Manga? by remember { mutableStateOf(null) }
            var windowState: WindowState by remember { mutableStateOf(WindowState.SEARCH) }
            var readerState by remember { mutableStateOf(ReaderState(0, 0, false, 0.0f, null)) }
            fun onReaderStateChange(state: ReaderState){
                readerState = state
            }

            fun onWindowStateChange(state: WindowState){
                windowState = state
            }

            fun notifyStateChange(state: ReaderState){
                reader?.notifyStateChange(state)
            }

            fun onChapterChange(chapter: Chapter){
                reader?.dispose()
                reader = Reader(chapter, ::onReaderStateChange)
                windowState = WindowState.VIEW
            }

            fun onMangaChange(manga: Manga){
                selectedManga = manga
                windowState = WindowState.INFO
            }

            AnimatedContent(
                targetState = windowState,
                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (targetState.ordinal > initialState.ordinal) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }
            ) { state ->
                when (state) {
                    WindowState.SEARCH -> SearchView(::onMangaChange)
                    WindowState.INFO -> MangaInfoView(selectedManga!!, ::onChapterChange, ::onWindowStateChange)
                    WindowState.VIEW -> ReaderView(readerState, ::notifyStateChange, ::onWindowStateChange)
                }
            }
        }
    }
}
