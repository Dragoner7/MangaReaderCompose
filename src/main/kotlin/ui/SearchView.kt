package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import api.model.Manga
import api.model.MangaDex
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Result(val manga : Manga, val cover : String?)

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun SearchView(onMangaChange : (Manga) -> Unit){
    Column(modifier = Modifier.fillMaxSize()) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        val mangaResults = remember { mutableStateListOf<Result>() }
        Row(modifier = Modifier.fillMaxWidth()){
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it }
            )
            Button(onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    val mangaList = MangaDex.getMangaByTitle(textState.value.text)
                    val covers = api.model.CoverStorage.getFirstCovers(mangaList)
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
    Box(
        modifier = Modifier.size(100.dp, 200.dp),
        contentAlignment = Alignment.Center,
    ){
        result.cover?.let { CoverImage(it) }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Text(
                text = result.manga.name,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Button(
                onClick = {
                onMangaChange(result.manga)
            }
            ){
                Text(text = "Read")
            }
        }
    }
}
