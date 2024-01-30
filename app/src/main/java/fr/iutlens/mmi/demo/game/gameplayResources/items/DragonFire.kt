package fr.iutlens.mmi.demo.game.gameplayResources.items

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DragonFire() : Item(
    image = R.drawable.dragon_fire,
    name = "Feu du dragon",
    description = "Blablablabla",
    effects = {
        game ->  game.controllableCharacter!!.projectile.onHitEffects = mutableListOf({
            val character = it
            if(character is Enemy){
                GlobalScope.launch {
                    repeat(5){
                        character.hit(game.controllableCharacter!!.projectile.damages, 0f, "static")
                    }
                }
            }
    })
    }
) {
}