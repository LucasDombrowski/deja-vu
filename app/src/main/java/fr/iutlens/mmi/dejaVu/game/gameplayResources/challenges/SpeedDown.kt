package fr.iutlens.mmi.dejaVu.game.gameplayResources.challenges

import fr.iutlens.mmi.dejaVu.game.gameplayResources.Challenge

class SpeedDown : Challenge(
    name = "Speed Down",
    effect = {
        game ->
        with(game.characterList.iterator()){
            forEach {
                it.speed/=1.5f
            }
        }
    },
    reverseEffect = {
        game ->
        with(game.characterList.iterator()){
            forEach {
                it.speed*=1.5f
            }
        }
    }
) {
}