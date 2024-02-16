package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI

class RangeNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(7),
    speed = 0.05f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(15,16,17),
    topAnimationSequence = listOf(33,34,35),
    bottomAnimationSequence = listOf(6,7,8),
    rightAnimationSequence = listOf(24,25,26),
    target = game.controllableCharacter!!,
    fireRate = 1500
){
    val projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,5), range = 4f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f)
    override fun spawn(x: Float, y: Float){
        game.addCharacter(this)
        changePos(x, y)
        reachPlayer()
    }

    override fun copy() : RangeNinja{
        val newCharacter = RangeNinja(sprite.x, sprite.y, game)
        newCharacter.sprite = newCharacter.sprite.copy()
        return newCharacter
    }

    fun reachPlayer(){
        action.cancel()
        action = setInterval(0,100){
            if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)>projectile.realRange(game)){
                moveTo(target!!.sprite.x, target!!.sprite.y)
            } else {
                shotPlayer()
            }
        }
    }

    fun shotPlayer(){
        action.cancel()
        stun()
        restart()
        action = setInterval(0,fireRate){
            GlobalScope.launch {
                while(game.pause){
                    delay(fireRate)
                }
                val center = getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
                val firstProjectile = rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (PI/6).toFloat())
                val secondProjectile = rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1],(-PI/6).toFloat())
                projectile.aimTarget(target!!, sprite.x, sprite.y)
                projectile.fireProjectile(game,sprite.x, sprite.y, firstProjectile[0], firstProjectile[1])
                projectile.fireProjectile(game,sprite.x,sprite.y,secondProjectile[0],secondProjectile[1])
                if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)>projectile.realRange(game)){
                    reachPlayer()
                }
            }

        }
    }

}