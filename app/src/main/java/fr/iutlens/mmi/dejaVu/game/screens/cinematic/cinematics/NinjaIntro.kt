package fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.MainCharacterPart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematicParts.NarratorPart

class NinjaIntro(game: Game, onEnd  : ()->Unit = {}) : Cinematic(
    parts = listOf(
        MainCharacterPart("Qu’est ce que ? Où suis-je ?"),
        NarratorPart("Blaise leva les yeux au ciel."),
        MainCharacterPart("Mais, je reconnais cette imposante allure aux couleurs noires ! C’est.. le château de Matsumoto, on m’aurait amené jusqu’à ses jardins ?"),
        NarratorPart("Très vite, le jeune héros se rend compte que ce n’était pas le seul problème."),
        MainCharacterPart("Impossible, le château est encore en construction ! Waouh mais qu’est ce que c’est que cet accoutrement, et cette arme ce n’est visiblement pas une réplique !"),
        NarratorPart("En effet, Blaise avait été téléporté en 1594 au cours de l’ère Sengoku au Japon. Construit entre 1593 et 1594, le château de Matsumoto est de nos jours l’un des douzes derniers authentiques châteaux du Japon. Célèbre pour sa couleur noire, il est surnommé “le corbeau” pour cette raison."),
        MainCharacterPart("Ne cède pas à la panique, allons voir au château si quelqu’un peut m’aider."),
    ),
    game = game,
    onEnd = onEnd
) {
}