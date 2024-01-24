package fr.iutlens.mmi.demo.game.sprite.sprites

import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class Attack(val sprite: BasicSprite, val speed: Float, val ranged: Boolean = true, val range: Float, val damages : Int, val fireRate: Long) {
    fun changePos(x: Float, y:Float){
        sprite.x = x
        sprite.y = y
    }

}
