package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class LoyaltyCard : Item(
    image = R.drawable.wallet,
    name = "Carte de fidélité du magasin",
    description = "Les prix du magasin sont maintenant réduits de moitié !",
    effects = {
        game ->  
    }
) {
}