package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class TutorialOpenRoom(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Bravo ! Vous avez vaincu tous les ennemis présents de la zone. La porte de sortie est à nouveau ouverte. Ouvrez l'œil, les objets au sol peuvent être récupérés, vous donnant accès à des nouvelles compétences mais aussi quelques surprises ! Attention, aucun retour en arrière ne sera possible après avoir quitté la pièce !",
            image = R.drawable.transparent,
            left = true,
            name = null
        ),
        CinematicPart(
            text = "La vie de Blaise se situe en haut à gauche de l’écran, si la totalité de conteneurs sont vides cela signifierait la fin de notre histoire.",
            image = R.drawable.transparent,
            left = true,
            name = null
        )
    ),
    game = game,
    onEnd = onEnd
) {
}