package ui

/*
import javafx.scene.image.Image
import model.Chapter
import model.Manga
import tornadofx.*

class ReaderView : View("Manga reader") {
    private lateinit var manga : Manga
    private lateinit var currentChapter : Chapter
    private var currentPageIndex : Int = 0

    private var pageView = imageview()
    private var titleLabel = label { }

    private val cache : MutableList<Image> = mutableListOf()

    private var toolbar = borderpane {
        left = button("Back to manga"){
            action {
                backToInfoView()
            }
        }
        center = titleLabel
        right = hbox{
            button("Previous"){
                action {
                    flipBack()
                }
            }
            button("Next") {
                action {
                    flipForward()
                }
            }
        }
    }

    private fun backToInfoView() {
        replaceWith(MangaInfoView::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
    }

    override val root = borderpane{
        top = toolbar
        center = pageView

        pageView.fitHeightProperty().bind(heightProperty() - toolbar.heightProperty())
        pageView.fitWidthProperty().bind(widthProperty())
        pageView.isPreserveRatio = true

        shortcut("Left"){
            flipBack()
        }
        shortcut("Right"){
            flipForward()
        }
    }

    private fun flipBack(){
        val newIndex = currentPageIndex - 1
        if(isNewPageValid(newIndex)){
            flipPage(newIndex)
        }
    }

    private fun flipForward(){
        val newIndex = currentPageIndex + 1
        if(isNewPageValid(newIndex)){
            flipPage(newIndex)
        }
    }

    private fun isNewPageValid(newIndex : Int) = newIndex >= 0 && newIndex < currentChapter.urls.size

    private fun flipPage(pageNumber : Int){
        currentPageIndex = pageNumber
        loadPage()
    }

    private fun loadPage(){
        val pageNumber = currentPageIndex
        pageView.image = cache[pageNumber]
    }

    fun onMangaLoad(manga : Manga){
        this.manga = manga
    }


    fun onChapterLoad(chapter : Chapter){
        this.currentChapter = chapter
        currentPageIndex = 0
        pageView.image = null
        titleLabel.text = "${manga.name} - $chapter"
        cache()
        loadPage()
    }

    private fun cache(){
        for(i in 0 until currentChapter.urls.size){
            cache.add(i, Image(currentChapter.urls[i], true))
        }
    }

}*/
