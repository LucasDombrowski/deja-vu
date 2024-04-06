package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Heart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialGoldHeart

class GoldHeartDrop(game : Game) : Collectible(
    game = game,
    spriteIndex = 4,
    sound = R.raw.grab_yellow_heart,
    collectEffect = {
        var toAdd = 1f
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
        if(game.firstTime && !checkedTutorials["goldHeart"]!!){
            game.athOverride["hearts"] = true
            game.cinematic.value = Pair(
                TutorialGoldHeart(game){
                    game.athOverride["hearts"] = false
                    commitBooleanSharedPreferences("goldHeartTutorial",true)
                },
                true
            )
        }
    }
){
}