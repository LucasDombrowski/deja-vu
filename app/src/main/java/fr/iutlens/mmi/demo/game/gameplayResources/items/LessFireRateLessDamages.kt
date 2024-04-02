package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class LessFireRateLessDamages() : Item(
    image = R.drawable.bonus_malus,
    name = "Échange équivalent",
    description = "Ce pacte vous permet d'accroître votre cadence de tir faisant de vous un véritable ninja! En échange, vous sacrifiez une partie de votre force. Cela vous remémore un manga que vous lisiez il y a longtemps…",
    effects =  {
        game ->  game.controllableCharacter!!.projectile.damages/=2
        game.controllableCharacter!!.fireRate = (game.controllableCharacter!!.fireRate.toFloat()/2f).toLong()
        game.controllableCharacter!!.fitToFireRate()
    }
) {
}