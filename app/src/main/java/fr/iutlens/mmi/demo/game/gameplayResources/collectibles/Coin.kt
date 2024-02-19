package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible

class Coin(game: Game) : Collectible(game, R.drawable.coin_drop, 0, collectEffect = {
    game.coins.value++
}) {
    fun drop(x: Float, y:Float){
        if((1..2).random()==1){
            setup(x,y)
        }
    }
}