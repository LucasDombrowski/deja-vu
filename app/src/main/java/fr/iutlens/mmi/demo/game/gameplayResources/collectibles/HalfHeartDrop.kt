package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible

class HalfHeartDrop(game : Game) : Collectible(
    game = game,
    spriteIndex = 3,
    sound = R.raw.grab_red_heart,
    collectEffect = {
        var toHeal = 0.5f
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