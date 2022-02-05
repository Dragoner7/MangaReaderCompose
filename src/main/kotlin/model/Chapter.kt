package model

import java.text.DecimalFormat

class Chapter(val id : String, number : Double, lang : String) {
    var number = number
        private set

    var lang = lang
        private set

    var title = ""

    val urls : List<String> by lazy { MangaDex.getChapterUrls(id) }

    override fun toString(): String {
        return "Chapter ${chapterNumberString()}: $title ($lang)"
    }

    fun chapterNumberString() : String{
        val nf = DecimalFormat("#.#")
        return nf.format(number)
    }


}