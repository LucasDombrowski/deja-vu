package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class VideoCinematic(game: Game) : Cinematic(
    listOf(
        MainCharacterPart("Où suis-je ?"),
        MainCharacterPart("Quelque chose brille là bas."),
        MainCharacterPart("AHHHHHHH !!!!"),
        NarratorPart("Et ce fut le début d'une incroyable aventure...")
    ),
    game
) {
}