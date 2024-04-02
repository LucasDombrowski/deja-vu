package fr.iutlens.mmi.dejaVu.game.gameplayResources.items

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Heart
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item

class NinjaScarf() : Item(
    image = R.drawable.ninja_scarf,
    name = "Écharpe de mamie",
    description = "Une écharpe tricotée avec amour, cela vous fait mal au cœur de la voler mais après tout rendre c’est aussi voler, non ? Vous obtenez des cœurs temporaires, l’effet disparaît une fois l’ensemble des cœurs détruits.",
    effects = {
        game ->  repeat(2){
            game.controllableCharacter!!.hearts.add(Heart(false))
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
) {
}