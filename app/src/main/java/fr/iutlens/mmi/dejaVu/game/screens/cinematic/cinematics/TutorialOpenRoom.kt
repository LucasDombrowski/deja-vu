package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class TutorialOpenRoom(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Bravo ! Vous avez vaincu tous les ennemis présents de la zone. La porte de sortie est à nouveau ouverte. Ouvrez l'œil, les objets au sol peuvent être récupérés, vous donnant accès à des nouvelles compétences mais aussi quelques surprises ! Attention, aucun retour en arrière ne sera possible après avoir quitté la pièce !",
            image = R.drawable.transparent,
            left = true,
            name = null,
        ),
        MainCharacterPart("Ouf, j’ai eu chaud !", onEnd = {
            game.athOverride["hearts"] = true
        }),
        CinematicPart(
            text = "La vie de Blaise se situe en haut à gauche de l’écran, si la totalité de conteneurs sont vides cela signifierait la fin de notre histoire.",
            image = R.drawable.transparent,
            left = true,
            name = null,
            onEnd = {
                game.athOverride["hearts"] = false
                game.athOverride["bar"] = true
            }
        ),
        NarratorPart("En bas à droite de votre écran s’affiche une barre de progression, celle-ci indique l’état d’avancée de Blaise dans sa fuite. Une fois le château atteint, la barre sera remplie, qui sait ce qui vous y attendra ?", onEnd = {
            game.athOverride["bar"] = false
        })
    ),
    game = game,
    onEnd = {
        game.athOverride["hearts"] = false
        game.athOverride["bar"] = false
        onEnd()
    }
) {
}