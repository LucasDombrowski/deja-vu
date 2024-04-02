package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class Torch : Item(
    image = R.drawable.torch,
    name = "Torche",
    description = "Vivre dans le noir peut être parfois contraignant, quoi de mieux qu’une flamme pour colorer votre vue. Obtenez une extension de votre éclairage, améliorant ainsi votre visibilité dans le noir ! Cette capacité ne disparaît pas.",
    effects = {
        game ->
        game.controllableCharacter!!.viewingDistance+=2
    }
) {
}