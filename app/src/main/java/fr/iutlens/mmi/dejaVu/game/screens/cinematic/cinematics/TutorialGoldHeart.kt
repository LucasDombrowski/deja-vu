package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class TutorialGoldHeart(game: Game, onEnd : ()->Unit =  {}) : Cinematic(
    listOf(
        NarratorPart("Vous avez ramassé un cœur temporaire. Celui-ci possède les mêmes propriétés qu’un cœur ordinaire, toutefois une fois le conteneur vide, l’effet disparaît. Vous trouverez des cœurs temporaires auprès d’ennemis vaincus ainsi qu’à la boutique.")
    ),
    game,
    onEnd = onEnd
) {
}