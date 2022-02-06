package ui

import WindowState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

@Composable
@Preview
fun ReaderView(readerState: ReaderState, onStateChange : (ReaderState) -> Unit, onWindowStateChange: (WindowState)-> Unit) {
    when(readerState.ready){
        true -> LoadedViewer(readerState, onStateChange, onWindowStateChange)
        false -> LoadingView(readerState.loadingProgress)
    }
}



@Composable
fun LoadedViewer(readerState: ReaderState, onStateChange : (ReaderState) -> Unit, onWindowStateChange: (WindowState)-> Unit){
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
            Row(modifier = Modifier.fillMaxSize()) {
                Button(onClick = {onWindowStateChange(WindowState.INFO)}){
                    Text(text = "Back")
                }
                Text(text = readerState.currentPageNumber.toString())
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
}