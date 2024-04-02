package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class TutorialOpenRoom(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Bravo ! Les ennemis de la salle sont tous éliminés, et la porte de sortie précédemment fermée s'est ouverte. N'oubliez pas de récupérer les collectables présents sur place, car aucun retour en arrière ne sera possible après avoir quitté la pièce. Veuillez aussi faire attention à votre état de vitalité, représenté par des coeurs en haut à gauche de l'écran : une totalité de conteneurs vides signifierait la fin de notre histoire.",
            image = R.drawable.transparent,
            left = true,
            name = null
        )
    ),
    game = game,
    onEnd = onEnd
) {
}