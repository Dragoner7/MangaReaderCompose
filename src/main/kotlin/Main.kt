// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Chapter
import model.Manga
import model.MangaDex
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Composable
@Preview
fun App(chapter: Chapter) {
    var readerState by remember { mutableStateOf(ReaderState(chapter, 0))}
    MaterialTheme {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    readerState = readerState.flip(-1)
                }) {
                    Text("Previous")
                }
                PageViewImage(readerState)
                Button(onClick = {
                    readerState = readerState.flip(1)
                }) {
                    Text("Next")
                }
            }
        }
    }
}

fun main() = application {
    //for some reason hybrid GPU Windows doesn't like Direct3D for Compose
    System.setProperty("skiko.renderApi", "OPENGL")
    //Neon Genesis Evangelion - Chapter 1 in a foreign language, depends on which order it's in the JSON
    var manga = remember { MangaDex.getManga("aaedcbda-ea61-4e7b-8143-7a475f327fbf") }
    var chapter = remember { manga!!.chapters[0] }
    Window(onCloseRequest = ::exitApplication) {
        App(chapter)
    }
}

@Composable
fun PageViewImage(readerState: ReaderState) {
    val image: ImageBitmap? by produceState<ImageBitmap?>(null, readerState) {
        value = withContext(Dispatchers.IO) {
            try {
                val imageRequest = Request.Builder().url(readerState.getCurrentPageUrl()).build()
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

