package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import model.CoverStorage
import model.Manga
import model.MangaDex
import java.awt.SystemColor.text

/*
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Paint
import model.CoverStorage
import model.Manga
import model.MangaDex
import tornadofx.*
import java.awt.Color

class SearchView : View("Search") {
    private val searchBox = textfield {}
    private val searchButton = button("Search") {
        action { search() }
        isDefaultButton = true
    }

    private val infoView by inject<MangaInfoView>()

    private val resultGrid = datagrid(emptyList<Result>()){
        cellCache{
            stackpane {
                it.cover?.let { e -> imageview(e){
                    fitHeight = 100.0
                    isPreserveRatio = true
                } }
                label(it.manga.name){
                    isWrapText = true
                    maxHeight = 100.0
                }
            }
        }
        singleSelect = true
        onUserSelect(1) {
            openInfoView(it.manga)
        }
    }

    private fun openInfoView(manga : Manga) {
        infoView.onMangaLoad(manga)
        replaceWith(MangaInfoView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
    }

    override val root = borderpane {
        top = borderpane {
            center = searchBox
            right = searchButton
        }
        center = resultGrid
    }


    private fun search(){
        root.center = label("Loading...")
        runAsync {
            searchButton.isDisable = true
            val mangaList = MangaDex.getMangaByTitle(searchBox.text)
            val covers = CoverStorage.getFirstCovers(mangaList)
            val results : MutableList<Result> = mutableListOf()
            for(manga in mangaList){
                val image = covers[manga]?.let { Image(it.url, true) }
                results.add(Result(manga, image))
            }
            searchButton.isDisable = false
            ui{
                root.center = resultGrid
                resultGrid.items.clear()
                resultGrid.items.addAll(results)
            }
        }
    }
}*/

class Result(val manga : Manga, val cover : String?)

@Composable
@Preview
fun SearchView(onMangaChange : (Manga) -> Unit){
    Column {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        val mangaResults = remember { mutableStateListOf<Result>() }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it }
        )
        Button(onClick = {
            GlobalScope.launch(Dispatchers.IO) {
                val mangaList = MangaDex.getMangaByTitle(textState.value.text)
                val covers = CoverStorage.getFirstCovers(mangaList)
                val results : MutableList<Result> = mutableListOf()
                for(manga in mangaList){
                    val image = covers[manga]?.url
                    results.add(Result(manga, image))
                }
                mangaResults.clear()
                mangaResults.addAll(results)
            }
        }){
            Text(text = "Search")
        }
        ResultsView(mangaResults, onMangaChange)
    }

}
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ResultsView(mangaList : List<Result>, onMangaChange : (Manga) -> Unit){
    LazyVerticalGrid(
        cells = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(mangaList){
            manga -> ResultView(manga, onMangaChange)
        }
    }
}

@Composable
fun ResultView(result: Result, onMangaChange : (Manga) -> Unit) {
    Box{
        result.cover?.let { CoverImage(it) }
        Text(text = result.manga.name)
        Button(onClick = {
            onMangaChange(result.manga)
        }){
            Text(text = "Read")
        }
    }
}
