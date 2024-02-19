package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class NinjaScarf() : Item(
    image = R.drawable.ninja_scarf,
    name = "Echarpe Ninja",
    description = "On rit beaucoup, on admira ; tel est l’esprit à l’usage de Verrières. Julien était déjà debout, tout le monde se leva malgré le décorum ; tel est l’empire du génie. Mme Valenod le retint encore un quart d’heure ; il fallait bien qu’il entendît les enfants réciter leur catéchisme ; ils firent les plus drôles de confusions, dont lui seul s’aperçut. Il n’eut garde de les relever. Quelle ignorance des premiers principes de la religion! pensait-il. Il saluait enfin et croyait pouvoir s’échapper ; mais il fallut essuyer une fable de La Fontaine.",
    effects = {
        game ->  repeat(2){
            game.controllableCharacter!!.hearts.add(Heart(false))
        }
        game.controllableCharacter!!.refreshHeathBar()
    }
) {
}