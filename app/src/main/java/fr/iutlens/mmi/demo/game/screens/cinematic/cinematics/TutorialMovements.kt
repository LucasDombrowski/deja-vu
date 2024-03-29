package fr.iutlens.mmi.demo.game.screens.cinematic.cinematics

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart

class TutorialMovements(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Pour vous déplacer, faîtes bouger le curseur en intéragissant avec l'écran. Blaise se mettra alors en mouvement vers celui-ci.",
            image = R.drawable.transparent,
            left = true,
            name = null
        )
    ),
    game = game,
    onEnd = onEnd
) {
}