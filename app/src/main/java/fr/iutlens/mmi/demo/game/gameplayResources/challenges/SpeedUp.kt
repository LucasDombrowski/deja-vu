package fr.iutlens.mmi.demo.game.gameplayResources.challenges

import android.util.Log
import fr.iutlens.mmi.demo.game.gameplayResources.Challenge

class SpeedUp : Challenge(
    name = "Speed up",
    effect = {
        game ->
        Log.i("speed up","true")
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