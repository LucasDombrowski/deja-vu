package fr.iutlens.mmi.demo.game.gameplayResources.challenges

import fr.iutlens.mmi.demo.game.gameplayResources.Challenge

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