package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart

class TutorialFighting(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        CinematicPart(
            text = "Dans votre fuite, vous croiserez des ennemis se montrant offensif envers vous, un combat n’est pas une option. Blaise cible et tire en se fiant à un marquage visible au sol, qui se déplacera toujours vers les ennemis les plus proches de lui.",
            image = R.drawable.transparent,
            left = true,
            name = null
        ),
        MainCharacterPart("J’ai jamais utilisé d’arme de ma vie mais bon, je crois que je n’ai pas le choix.")
    ),
    game = game,
    onEnd = onEnd
) {
}