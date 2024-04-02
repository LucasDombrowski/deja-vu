package fr.iutlens.mmi.dejaVu.game.gameplayResources.items

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item

class LoyaltyCard : Item(
    image = R.drawable.wallet,
    name = "Carte de fidélité",
    description = "Être un client fidèle n’est pas propre à votre époque. Ce laissez-passer divise les prix de la boutique par deux. N’oubliez pas de remercier le marchand.",
    effects = {
        game ->  
    }
) {
}