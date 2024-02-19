package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart

class HeartContainer(game: Game): Collectible(game, R.drawable.heart_container,0, collectEffect = {
    game.controllableCharacter!!.hearts.add(Heart(true,1f))
    game.controllableCharacter!!.hearts.forEach {
        if(it.permanent){
            it.filled = 1f
        }
    }
    game.controllableCharacter!!.refreshHeathBar()
})