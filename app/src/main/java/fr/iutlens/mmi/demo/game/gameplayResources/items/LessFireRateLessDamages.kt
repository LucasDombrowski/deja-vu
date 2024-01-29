package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item

class LessFireRateLessDamages() : Item(
    image = R.drawable.bonus,
    name = "Cadence augmentée, dégats réduits",
    description = "Blablablablablabla",
    effects =  {
            game ->  game.controllableCharacter!!.projectile.damages/=2
        game.controllableCharacter!!.fireRate = (game.controllableCharacter!!.fireRate.toFloat()/2f).toLong()
        game.controllableCharacter!!.fitToFireRate()
    }
) {
}