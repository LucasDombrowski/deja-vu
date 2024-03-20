package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class Wallet : Item(
    image = R.drawable.wallet,
    name = "Portefeuille",
    description = "Augmente la probabilité de trouver des pièces améliorées, ajoute un montant aléatoire à vos pièces actuelles",
    effects = {
        game ->
        if(game.superCoinDropProbability<5){
            game.superCoinDropProbability++
        }
        game.coins.value+=(25..50).random()
    }
) {
}