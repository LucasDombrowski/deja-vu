package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class NarratorPart(text : String, highlightedWords : List<String> = listOf(), onEnd : ()->Unit = {}) : CinematicPart(
    text = text,
    image = R.drawable.transparent,
    left = true,
    highlightedWords = highlightedWords,
    onEnd = onEnd
) {
}