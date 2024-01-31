package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import kotlin.math.PI

class NinjaShuriken() : Item(
    image = R.drawable.sandal,
    name = "Shuriken Ninja",
    description = "Blablabla",
    effects = {
        game ->  game.controllableCharacter!!.directProjectileBehaviors.add {
        val center = getCenter(game.controllableCharacter!!.target!!.sprite.x, game.controllableCharacter!!.target!!.sprite.y, game.controllableCharacter!!.sprite.x, game.controllableCharacter!!.sprite.y)
        val firstProjectile = rotationFromPoint(game.controllableCharacter!!.target!!.sprite.x,
            game.controllableCharacter!!.target!!.sprite.y,
            center[0],
            center[1],
            (PI / 6).toFloat()
        )
        val secondProjectile = rotationFromPoint(
            game.controllableCharacter!!.target!!.sprite.x,
            game.controllableCharacter!!.target!!.sprite.y,
            center[0],
            center[1],
            (-PI / 6).toFloat()
        )
        game.controllableCharacter!!.projectile.fireProjectile(game,game.controllableCharacter!!.sprite.x, game.controllableCharacter!!.sprite.y, firstProjectile[0], firstProjectile[1])
        game.controllableCharacter!!.projectile.fireProjectile(game,game.controllableCharacter!!.sprite.x,game.controllableCharacter!!.sprite.y,secondProjectile[0],secondProjectile[1])
    }
    }
) {
}