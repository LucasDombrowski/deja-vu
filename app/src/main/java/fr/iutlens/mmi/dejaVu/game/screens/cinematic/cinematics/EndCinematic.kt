package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart

class EndCinematic(game: Game, onEnd : ()->Unit) : Cinematic(
    listOf(
        CinematicPart(
            text = "Qu’est ce que c’était ça ? J’ai eu chaud ! Bon, le livre est apparu à nouveau. Si je suis la logique j’ai lu le livre et je suis tombé dedans, donc techniquement je devrais avoir fini, j’ai battu le grand méchant, il ne reste plus que la conclusion où tout le monde finit heureux, non ?",
            image = R.drawable.cinematic_character,
            left = true,
            name = "Blaise",
            imageSliceX = 2,
            imageAnimationDelay = 200
        )
    ),
    game,
    onEnd = onEnd
){
}