package fr.iutlens.mmi.dejaVu.game.screens.cinematic

open class CinematicPart(val text: String, val image: Int, val left: Boolean, val name : String ? = null, val imageSliceX : Int = 1, val imageSliceY : Int = 1, val imageAnimationDelay : Long = 100L, val highlightedWords : List<String> = listOf<String>(), val italicWords : List<String> = listOf(), val onEnd : ()->Unit = {}) {

}