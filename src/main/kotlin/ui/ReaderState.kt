package ui

import androidx.compose.ui.graphics.ImageBitmap

data class ReaderState(val currentPageNumber : Int, val numOfPages : Int, val ready : Boolean, val loadingProgress : Float) {

    fun flip(num : Int) : ReaderState {
        if(currentPageNumber + num in 0 until numOfPages){
            return ReaderState(currentPageNumber + num, numOfPages, ready, loadingProgress).also { it.downloadedPages = downloadedPages }
        }
        return this
    }

    var downloadedPages : List<ImageBitmap>  = emptyList()
}