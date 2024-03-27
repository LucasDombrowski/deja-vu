package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart

class HalfGoldHeartDrop(game : Game) : Collectible(
    game = game,
    spriteIndex = 5,
    sound = R.raw.grab_yellow_heart,
    collectEffect = {
        var toAdd = 0.5f
        for(heart in game.controllableCharacter!!.hearts){
            if(!heart.permanent && heart.filled<1f){
                while (heart.filled<1f && toAdd>0f){
                    heart.filled+=0.25f
                    toAdd-=0.25f
                }
            }
            if(toAdd<=0f){
                break
            }
        }
        if(toAdd>0f){
            while (toAdd>0f){
                val newHeart = Heart(permanent = false, filled = 0f)
                while (newHeart.filled<1f && toAdd>0f){
                    newHeart.filled+=0.25f
                    toAdd-=0.25f
                }
                game.controllableCharacter!!.hearts.add(newHeart)
            }
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
){
}