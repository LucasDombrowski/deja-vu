package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class NinjaScarf() : Item(
    image = R.drawable.ninja_scarf,
    name = "Echarpe Ninja",
    description = "Blablabla",
    effects = {
        game ->  repeat(2){
            game.controllableCharacter!!.hearts.add(Heart(false))
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
) {
}