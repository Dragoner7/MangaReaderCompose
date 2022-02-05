package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Chapter
import model.CoverStorage
import model.Manga
import okhttp3.OkHttpClient
import okhttp3.Request

/*
import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import model.Chapter
import model.CoverStorage
import model.Manga
import tornadofx.*

class MangaInfoView : View("Manga info") {
    private val coverView = imageview {
        fitWidth = 200.0
        isPreserveRatio = true
    }

    private val descriptionLabel = textarea {
        isWrapText = true
        isEditable = false
    }

    private val nameLabel = label {
        style{
            fontSize = Dimension(24.0, Dimension.LinearUnits.px)
        }
        maxWidthProperty().bind(descriptionLabel.widthProperty())
        isWrapText = true
    }
    private val chapterListView = listview<Chapter> {
        selectionModel.selectionMode = SelectionMode.SINGLE
    }

    private val infoView = borderpane {
        left = borderpane {
            top = nameLabel
            center = borderpane {
                center = coverView
            }
            bottom = descriptionLabel
        }
        center = chapterListView
        chapterListView.onUserSelect(2) { e -> openReader(e) }
    }

    private val loadingLabel = label("Loading...")
    private val readerView : ReaderView by inject()


    private lateinit var manga : Manga

    override val root = borderpane {
        top = button("Back to search") {
            action {
                backToInfoView()
            }
        }
        center = loadingLabel
    }
    fun onMangaLoad(manga : Manga){
        this.manga = manga
        refreshView()
    }

    private fun refreshView() {
        root.center = loadingLabel
        nameLabel.text = manga.name
        descriptionLabel.text = manga.description
        runAsync {
            val chapters = manga.chapters.asObservable()
            val cover = CoverStorage.getFirstCover(manga)
            println("sorted")
            ui{
                coverView.image = Image(cover?.url, true)
                chapterListView.items.clear()
                chapterListView.items.addAll(chapters)
                root.center = infoView
            }
        }
    }

    private fun openReader(chapter: Chapter){
        readerView.onMangaLoad(manga)
        readerView.onChapterLoad(chapter)
        replaceWith(ReaderView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
    }

    private fun backToInfoView() {
        replaceWith(SearchView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
    }

}*/

@Composable
@Preview
fun MangaInfoView(manga: Manga, onChapterSelect: (Chapter)-> Unit){
    val chapterList = remember { manga.chapters }
    Row {
        Column(Modifier.width(250.dp)) {
            Text(
                text = manga.name,
                fontSize = 24.sp
            )
            CoverStorage.getFirstCover(manga)?.url?.let { CoverImage(it) }
            Text(
                text = manga.description,
                fontSize = 16.sp
            )
        }
        Box{
            val state = rememberLazyListState()
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
                items(chapterList){
                        chapter -> ChapterView(chapter, onChapterSelect)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = state)
            )
        }
    }
}

@Composable
fun ChapterView(chapter : Chapter, onChapterSelect :(Chapter)->Unit){
    Button(
        onClick = {
            onChapterSelect(chapter)
        },
    ){
        Text(
            text = chapter.toString(),
            fontSize = 16.sp
        )
    }
}

@Composable
fun CoverImage(url : String) {
    val image: ImageBitmap? by produceState<ImageBitmap?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                val imageRequest = Request.Builder().url(url).build()
                val client = OkHttpClient.Builder().build()
                val response = client.newCall(imageRequest).execute().body()?.bytes()
                org.jetbrains.skia.Image.makeFromEncoded(response).toComposeImageBitmap()

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