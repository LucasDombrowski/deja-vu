package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class TutorialCoins(game: Game, onEnd : ()->Unit =  {}) : Cinematic(
    listOf(
        NarratorPart("Il est possible que vos ennemis abandonnent leurs pièces. N’hésitez pas à les récupérer pour augmenter vos gains. Les pièces jaunes valent une pièce, et les pièces argentés équivalent à 5 ! Vous pouvez dépenser cet argent à la boutique.")
    ),
    game,
    onEnd = onEnd
) {
}