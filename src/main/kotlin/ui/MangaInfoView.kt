package ui

import WindowState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.model.Chapter
import api.model.Cover
import api.model.CoverStorage
import api.model.Manga
import kotlinx.coroutines.*
import java.awt.Window
import java.net.URL
import kotlin.coroutines.CoroutineContext

@Composable
@Preview
fun MangaInfoView(manga: Manga, onChapterSelect: (Chapter)-> Unit, onWindowStateChange: (WindowState)-> Unit){
    var ready by remember { mutableStateOf<Boolean>(false) }
    val coroutineScope = rememberCoroutineScope()
    if(ready){
        MangaView(manga, onChapterSelect, onWindowStateChange)
    } else {
        coroutineScope.launch(Dispatchers.IO) {
            manga.chapters
            CoverStorage.getFirstCover(manga)
            ready = true
        }
        LoadingView(0.0f)
    }

}

@Composable
fun MangaView(manga: Manga, onChapterSelect: (Chapter)-> Unit, onWindowStateChange: (WindowState)-> Unit){
    Row {
        Button(onClick = {onWindowStateChange(WindowState.SEARCH)}){
            Text(text = "Back")
        }
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
        Box(Modifier.fillMaxWidth()){
            val state = rememberLazyListState()
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
                items(manga.chapters.filter { ch->!ch.external }){
                        chapter -> ChapterView(chapter, manga.chapters.indexOf(chapter) ,onChapterSelect)
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
fun ChapterView(chapter : Chapter, index : Int,onChapterSelect :(Chapter)->Unit){
    Card(
        modifier = Modifier.padding(12.dp).fillMaxWidth(),
    ) {
        TextButton(onClick = {
            onChapterSelect(chapter)
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)){
            Text(
                text = chapter.toString(),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CoverImage(url : String) {
    val image: ImageBitmap? by produceState<ImageBitmap?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                URL(url).openStream().buffered().use(::loadImageBitmap)
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