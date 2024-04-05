package fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Enemy
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Projectile
import fr.iutlens.mmi.dejaVu.utils.getCenter
import fr.iutlens.mmi.dejaVu.utils.rotationFromPoint
import fr.iutlens.mmi.dejaVu.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI

class RangeNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.range_ninja,x,y,0),
    game = game,
    basicAnimationSequence = listOf(0),
    speed = 0.08f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(12,13,14,15),
    topAnimationSequence = listOf(6,7),
    bottomAnimationSequence = listOf(2,3),
    rightAnimationSequence = listOf(8,9,10,11),
    target = game.controllableCharacter!!,
    animationDelay = 100L,
    fireRate = 1500
){
    val projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,5), range = 6f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f, sound = R.raw.ninja_shot)

    var shotAwaitTick = fireRate

    var delayPatternTime = 100

    override fun basicAnimation() : List<Int>{
        basicAnimationSequence = when(previousDirection){
            "left"-> listOf(4)
            "right"-> listOf(0)
            else->basicAnimationSequence
        }
        return basicAnimationSequence
    }
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
        disablePathFollowing()
        action.cancel()
        action = setInterval(0,delayPatternTime.toLong()){
            if(!game.pause) {
                if (distanceWith(target!!) > projectile.realRange(game) || !isPathFree(
                        target!!.sprite.x,
                        target!!.sprite.y
                    )
                ) {
                    if (!isPathFree(target!!.sprite.x, target!!.sprite.y)) {
                        followPlayer()
                    } else {
                        moveTo(target!!.sprite.x, target!!.sprite.y)
                    }
                } else {
                    shotPlayer()
                }
            }
        }
    }

    fun followPlayer(){
        action.cancel()
        setupPath(target!!.sprite.x, target!!.sprite.y)
        pathFollow = true
        action = setInterval(0,delayPatternTime.toLong()){
            if(!game.pause && (isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow)){
                reachPlayer()
            }
        }
    }

    fun shotPlayer(){
        disablePathFollowing()
        action.cancel()
        stun()
        restart()
        if(!game.ended) {
            action = setInterval(0, delayPatternTime.toLong()) {
                if (!game.pause) {
                    if (distanceWith(target!!) > projectile.realRange(game) || !isPathFree(
                            target!!.sprite.x,
                            target!!.sprite.y
                        )
                    ) {
                        reachPlayer()
                    } else if(target!!.inBoundingBox(sprite.x,sprite.y)) {
                        target!!.healthDown(projectile.damages, projectile.knockback, currentDirection)
                    } else if(shotAwaitTick>=fireRate){
                        currentAnimationSequence = basicAnimation()
                        shotAwaitTick = 0
                        val center =
                            getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
                        val firstProjectile = rotationFromPoint(
                            target!!.sprite.x,
                            target!!.sprite.y,
                            center[0],
                            center[1],
                            (PI / 6).toFloat()
                        )
                        val secondProjectile = rotationFromPoint(
                            target!!.sprite.x,
                            target!!.sprite.y,
                            center[0],
                            center[1],
                            (-PI / 6).toFloat()
                        )
                        projectile.aimTarget(target!!, sprite.x, sprite.y)
                        projectile.fireProjectile(
                            game,
                            sprite.x,
                            sprite.y,
                            firstProjectile[0],
                            firstProjectile[1]
                        )
                        projectile.fireProjectile(
                            game,
                            sprite.x,
                            sprite.y,
                            secondProjectile[0],
                            secondProjectile[1]
                        )
                    } else {
                        currentAnimationSequence = basicAnimation()
                        shotAwaitTick += delayPatternTime
                    }
                }
            }
        }
    }

}