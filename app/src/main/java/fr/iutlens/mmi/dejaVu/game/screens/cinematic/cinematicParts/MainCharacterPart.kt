package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class MainCharacterPart(text : String, highlightedWords : List<String> = listOf(), italicWords : List<String> = listOf()) : CinematicPart(
    text = text,
    image = R.drawable.cinematic_character,
    left = true,
    name = "Blaise",
    imageSliceX = 2,
    imageAnimationDelay = 200,
    highlightedWords = highlightedWords,
    italicWords = italicWords
) {
}