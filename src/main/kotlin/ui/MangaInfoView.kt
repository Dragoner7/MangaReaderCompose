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
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.model.Manga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
@Preview
fun MangaInfoView(manga: Manga, onChapterSelect: (api.model.Chapter)-> Unit){
    val chapterList = remember { manga.chapters }
    Row {
        Column(Modifier.width(250.dp)) {
            Text(
                text = manga.name,
                fontSize = 24.sp
            )
            api.model.CoverStorage.getFirstCover(manga)?.url?.let { CoverImage(it) }
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
fun ChapterView(chapter : api.model.Chapter, onChapterSelect :(api.model.Chapter)->Unit){
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