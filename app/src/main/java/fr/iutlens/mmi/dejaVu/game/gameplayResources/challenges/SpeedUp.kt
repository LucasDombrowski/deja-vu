package fr.iutlens.mmi.dejaVu.game.gameplayResources.challenges

import android.util.Log
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Challenge

class SpeedUp : Challenge(
    name = "Speed up",
    effect = {
        game ->
        with(game.characterList.iterator()){
            forEach {
                it.speed*=1.5f
            }
        }
    },
    reverseEffect = {
        game ->
        with(game.characterList.iterator()){
            forEach {
                it.speed/=1.5f
            }
        }
    }
) {
}