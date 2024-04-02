package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class NinjaBoots : Item(
    image = R.drawable.sandal,
    name = "Sandales en bois",
    description = "Chaussez-vous de ces belles sandales et surprenez vos ennemis par votre vitesse époustouflante. Raiden serait fier de vous.",
    effects = {
        game -> game.controllableCharacter!!.speed+=0.05f
    }
) {
}