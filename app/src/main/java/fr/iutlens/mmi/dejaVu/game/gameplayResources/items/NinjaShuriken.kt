package fr.iutlens.mmi.dejaVu.game.gameplayResources.items

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item
import fr.iutlens.mmi.dejaVu.utils.getCenter
import fr.iutlens.mmi.dejaVu.utils.rotationFromPoint
import kotlin.math.PI

class NinjaShuriken() : Item(
    image = R.drawable.ninja_shuriken,
    name = "Triple shuriken",
    description = "Triplez vos tirs pour toucher un maximum d’ennemis. On raconte que cette arme appartenait à célèbre ninja…",
    effects = {
        game ->  game.controllableCharacter!!.directProjectileBehaviors.add {
        val center = getCenter(game.controllableCharacter!!.aimOffset().first, game.controllableCharacter!!.aimOffset().second, game.controllableCharacter!!.sprite.x, game.controllableCharacter!!.sprite.y)
        val firstProjectile = rotationFromPoint(game.controllableCharacter!!.aimOffset().first,
            game.controllableCharacter!!.aimOffset().second,
            center[0],
            center[1],
            (PI / 6).toFloat()
        )
        val secondProjectile = rotationFromPoint(
            game.controllableCharacter!!.aimOffset().first,
            game.controllableCharacter!!.aimOffset().second,
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