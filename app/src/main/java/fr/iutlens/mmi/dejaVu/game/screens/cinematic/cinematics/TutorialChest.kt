package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart

class TutorialChest(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Un coffre dont le contenu est inconnu se dresse au centre de la salle. Oserez-vous l'ouvrir pour découvrir ce qu'il renferme ?",
            image = R.drawable.transparent,
            left = true,
            name = null
        ),
        MainCharacterPart("C’est peut-être un piège, si je fais comme dans The Legend of Zeldo j’obtiendrai sans doute un objet ! Mais non réveille toi Blaise, tu n’es pas certainement dans un jeu d’aventure !", italicWords = listOf("The Legend of Zeldo"))
    ),
    game = game,
    onEnd = onEnd
) {
}