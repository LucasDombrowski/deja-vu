package fr.iutlens.mmi.dejaVu.game.gameplayResources.items

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item

class MoreDamagesMoreRate() : Item(
    image = R.drawable.bonus_malus,
    name = "Contrepartie",
    description = "Aujourd’hui, c’est votre jour de chance, votre force a été décuplée… Malheureusement, le destin n’est pas si clément et vous impose une contrepartie. Vos bras flasques ne parviennent plus à tirer aussi rapidement.",
    effects =  {
        game ->  game.controllableCharacter!!.projectile.damages*=2
        game.controllableCharacter!!.fireRate = (game.controllableCharacter!!.fireRate.toFloat()*2f).toLong()
        game.controllableCharacter!!.fitToFireRate()
    }
) {
}