package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class TutorialMovements(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        NarratorPart("Vous êtes arrivé dans un monde hostile où vous devez vous échapper. Afin de vous déplacer, un Grimoire Blanc vous montrera le chemin. Faites le bouger en interagissant avec l'écran. Blaise se déplacera instinctivement vers celui-ci, quelle confiance aveugle !", highlightedWords = listOf("Grimoire Blanc")),
        MainCharacterPart("Mes vêtements ont peut-être changé, mais je ne semble pas avoir perdu mon habilité. Essayons !")
    ),
    game = game,
    onEnd = onEnd
) {
}