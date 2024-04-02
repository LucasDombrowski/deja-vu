package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class HandHeart : Item(
    image = R.drawable.hand_heart,
    name = "Coeur sur la main",
    description = "Votre altruisme a été vivement remarqué. Les ennemis semblent être plus clément avec vous lors de leur dernier souffle. La probabilité d’obtenir plus de cœurs auprès d’un ennemi augmente.",
    effects = {
        game ->
        if(game.heartDropProbability + 1 >= 5){
            game.heartDropProbability = 5
        } else {
            game.heartDropProbability+=1
        }

        var toHeal = 2f
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