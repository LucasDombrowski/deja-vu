package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class NinjaBoots : Item(
    image = R.drawable.sandal,
    name = "Sandales de Ninja",
    description = "",
    effects = {
        game -> game.controllableCharacter!!.speed+=0.05f
    }
) {
}