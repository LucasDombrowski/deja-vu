package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class TutorialMovements(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Vous êtes arrivé dans un monde hostile où vous devez vous échapper. Afin de vous déplacer, faites bouger le curseur en interagissant avec l'écran. Blaise se déplacera instinctivement vers celui-ci.",
            image = R.drawable.transparent,
            left = true,
            name = null
        )
    ),
    game = game,
    onEnd = onEnd
) {
}