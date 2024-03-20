package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible

class HeartDrop(game : Game) : Collectible(
    game = game,
    spriteIndex = 2,
    collectEffect = {
        var toHeal = 1f
        for(heart in game.controllableCharacter!!.hearts){
            if(heart.permanent && heart.filled<1f){
                while (heart.filled<1f && toHeal>0f){
                    heart.filled+=0.25f
                    toHeal-=0.25f
                }
            }
            if(toHeal<=0f){
                break
            }
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
) {
}