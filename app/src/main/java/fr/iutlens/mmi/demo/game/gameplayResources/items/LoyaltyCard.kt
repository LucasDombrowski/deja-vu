package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class LoyaltyCard : Item(
    image = R.drawable.wallet,
    name = "Carte de fidélité",
    description = "Être un client fidèle n’est pas propre à votre époque. Ce laissez-passer divise les prix de la boutique par deux. N’oubliez pas de remercier le marchand.",
    effects = {
        game ->  
    }
) {
}