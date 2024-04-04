package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class NinjaBossPart(text : String, reveal : Boolean = false) : CinematicPart(
    text = text,
    image = R.drawable.transparent,
    left = false,
    name = when(reveal){
        false -> "????"
        else -> "Corbeau"
    }
) {
}