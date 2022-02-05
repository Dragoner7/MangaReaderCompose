package ui
/*

import javafx.scene.Parent
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import model.Chapter
import model.CoverStorage
import model.Manga
import tornadofx.*

class MangaInfoView : View("Manga info") {
    private val coverView = imageview {
        fitWidth = 200.0
        isPreserveRatio = true
    }

    private val descriptionLabel = textarea {
        isWrapText = true
        isEditable = false
    }

    private val nameLabel = label {
        style{
            fontSize = Dimension(24.0, Dimension.LinearUnits.px)
        }
        maxWidthProperty().bind(descriptionLabel.widthProperty())
        isWrapText = true
    }
    private val chapterListView = listview<Chapter> {
        selectionModel.selectionMode = SelectionMode.SINGLE
    }

    private val infoView = borderpane {
        left = borderpane {
            top = nameLabel
            center = borderpane {
                center = coverView
            }
            bottom = descriptionLabel
        }
        center = chapterListView
        chapterListView.onUserSelect(2) { e -> openReader(e) }
    }

    private val loadingLabel = label("Loading...")
    private val readerView : ReaderView by inject()


    private lateinit var manga : Manga

    override val root = borderpane {
        top = button("Back to search") {
            action {
                backToInfoView()
            }
        }
        center = loadingLabel
    }
    fun onMangaLoad(manga : Manga){
        this.manga = manga
        refreshView()
    }

    private fun refreshView() {
        root.center = loadingLabel
        nameLabel.text = manga.name
        descriptionLabel.text = manga.description
        runAsync {
            val chapters = manga.chapters.asObservable()
            val cover = CoverStorage.getFirstCover(manga)
            println("sorted")
            ui{
                coverView.image = Image(cover?.url, true)
                chapterListView.items.clear()
                chapterListView.items.addAll(chapters)
                root.center = infoView
            }
        }
    }

    private fun openReader(chapter: Chapter){
        readerView.onMangaLoad(manga)
        readerView.onChapterLoad(chapter)
        replaceWith(ReaderView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
    }

    private fun backToInfoView() {
        replaceWith(SearchView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
    }

}*/
