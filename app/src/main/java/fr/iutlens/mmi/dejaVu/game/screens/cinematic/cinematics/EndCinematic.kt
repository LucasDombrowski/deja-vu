package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart

class EndCinematic(game: Game, onEnd : ()->Unit) : Cinematic(
    listOf(
        MainCharacterPart("Les conséquences, n’est ce pas? Que veut-il dire ? Dans tous les cas j’ai eu chaud !"),
        MainCharacterPart("Bon, le livre est apparu à nouveau. Si je suis la logique j’ai lu le livre et je suis tombé dedans, donc techniquement je devrais avoir fini, j’ai battu le grand méchant, il ne reste plus que la conclusion où tout le monde finit heureux, non ?")
    ),
    game,
    onEnd = onEnd
){
}