package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class Torch : Item(
    image = R.drawable.torch,
    name = "Torche",
    description = "Portée de vision augmentée dans les endroits sombres.",
    effects = {
        game ->
        game.controllableCharacter!!.viewingDistance+=2
    }
) {
}