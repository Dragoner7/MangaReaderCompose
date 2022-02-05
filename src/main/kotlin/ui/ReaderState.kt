package ui

import model.Chapter

data class ReaderState(val chapter : Chapter, val currentPageNumber : Int) {
    private var currentPageUrl = chapter.urls[currentPageNumber]

    fun flip(num : Int) : ReaderState {
        if(chapter.urls.size > currentPageNumber + num){
            return ReaderState(chapter, currentPageNumber + num)
        }
        return this
    }

    fun getCurrentPageUrl(): String {
        return currentPageUrl
    }
}