package ui

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
