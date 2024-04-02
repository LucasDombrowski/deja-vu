package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item


class OneHeart : Item(
    image = R.drawable.permanent_heart_4_4,
    name = "Réceptacle de cœur",
    description = "Vous avez triomphé d’un ennemi redoutable, votre courage et votre bravoure ont été récompensés. Votre vie a été régénérée, profitez de ce repos bien mérité.",
    effects = {
        game ->
        game.controllableCharacter!!.hearts.add(Heart(true,1f))
        game.controllableCharacter!!.hearts.forEach {
            if(it.permanent){
                it.filled = 1f
            }
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
) {
}