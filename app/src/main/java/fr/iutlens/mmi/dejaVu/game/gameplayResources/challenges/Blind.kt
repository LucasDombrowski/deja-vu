package fr.iutlens.mmi.dejaVu.game.gameplayResources.challenges

import fr.iutlens.mmi.dejaVu.game.gameplayResources.Challenge

class Blind : Challenge(
    name = "Blind",
    effect = {
        game ->
        game.blinded = true
    },
    reverseEffect = {
        game ->
        game.blinded = false
    }
) {
}