package fr.iutlens.mmi.demo.game.screens.cinematic.cinematics

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.demo.game.screens.cinematic.CinematicPart

class TutorialChest(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Un coffre dont le contenu est inconnu se dresse au centre de la salle. Oserez-vous l'ouvrir pour obtenir ce qu'il renferme ?",
            image = R.drawable.transparent,
            left = true,
            name = null
        )
    ),
    game = game,
    onEnd = onEnd
) {
}