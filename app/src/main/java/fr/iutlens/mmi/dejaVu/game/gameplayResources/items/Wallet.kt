package fr.iutlens.mmi.dejaVu.game.gameplayResources.items

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item

class Wallet : Item(
    image = R.drawable.wallet,
    name = "Carte bancaire",
    description = "Quoi de mieux dans la vie que d’obtenir de l’argent? En posséder encore plus ! Débloquer un montant aléatoire de pièces, et augmentez vos chances de décrocher des pièces argentées.",
    effects = {
        game ->
        if(game.superCoinDropProbability<5){
            game.superCoinDropProbability++
        }
        game.coins.value+=(25..50).random()
    }
) {
}