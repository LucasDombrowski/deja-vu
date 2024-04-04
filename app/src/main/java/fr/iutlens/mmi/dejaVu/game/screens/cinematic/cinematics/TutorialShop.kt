package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart

class TutorialShop(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Bienvenue dans la boutique ! Dirigez Blaise vers un article pour l'acheter avec les pièces récoltées jusqu'à présent.",
            image = R.drawable.transparent,
            left = true,
            name = null
        ),
        MainCharacterPart("Hm, que vais-je acheter ?")
    ),
    game = game,
    onEnd = onEnd
) {
}