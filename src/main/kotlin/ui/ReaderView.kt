package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Chapter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.skia.Image

@Composable
@Preview
fun ReaderView(readerState: ReaderState, onStateChange : (ReaderState) -> Unit) {
    when(readerState.ready){
        true -> LoadedViewer(readerState, onStateChange)
        false -> CircularProgressIndicator(readerState.loadingProgress)
    }
}

@Composable
fun LoadedViewer(readerState: ReaderState, onStateChange : (ReaderState) -> Unit){
    val image by produceState<ImageBitmap?>(null, readerState){
        value = readerState.currentPage
    }
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ){
                if(image != null){
                    Image(
                        painter = BitmapPainter(image!!),
                        contentDescription = null,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(onClick = {
                    onStateChange(readerState.flip(-1))
                }) {
                    Text("Previous")
                }
                Button(onClick = {
                    onStateChange(readerState.flip(1))
                }) {
                    Text("Next")
                }
            }
        }
    }
}