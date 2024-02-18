package fr.iutlens.mmi.demo.game.sprite.sprites

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import java.util.LinkedList
import java.util.Queue

open class Enemy(
    sprite: BasicSprite,
    game: Game,
    speed:Float,
    hearts: MutableList<Heart>,
    basicAnimationSequence: List<Int>,
    leftAnimationSequence: List<Int> = basicAnimationSequence,
    topAnimationSequence: List<Int> = basicAnimationSequence,
    rightAnimationSequence: List<Int> = basicAnimationSequence,
    bottomAnimationSequence: List<Int> = basicAnimationSequence,
    target: Character? = null,
    fireRate: Long = 0,
    open var action: Job = GlobalScope.launch {
        return@launch
    },
    var onDeath:  ()->Unit = {}
) : Character(
    sprite = sprite,
    game = game,
    speed = speed,
    hearts = hearts,
    basicAnimationSequence = basicAnimationSequence,
    leftAnimationSequence = leftAnimationSequence,
    topAnimationSequence = topAnimationSequence,
    rightAnimationSequence = rightAnimationSequence,
    bottomAnimationSequence = bottomAnimationSequence,
    target = target,
    fireRate = fireRate
){
    open fun hit(damages: Float, knockback: Float, direction: String){
        healthDown(damages, knockback, direction)
        GlobalScope.launch {
            sprite.semiWhiteColor()
            delay(100)
            sprite.permanentColor()
            if(filledHeart()<=hearts.size/2 && filledHeart()>hearts.size/4){
                sprite.midLifeColor()
            } else if(filledHeart()<=hearts.size/4){
                sprite.lowLifeColor()
            }
        }

    }

    open fun spawn(x: Float, y: Float){
        game.addCharacter(this)
        changePos(x, y)
    }

    override fun copy() : Enemy{
        return Enemy(sprite.copy(), game, speed, hearts, basicAnimationSequence, leftAnimationSequence, topAnimationSequence, rightAnimationSequence, bottomAnimationSequence, target, fireRate, action)
    }


    fun filledHeart() : Int{
        return hearts.filter {
            it.filled>0f
        }.size
    }

    fun smokeAnimation(){
        val smoke = BasicSprite(R.drawable.smoke_animation,sprite.x,sprite.y)
        game.addSprite(smoke)
        GlobalScope.launch {
            repeat(5){
                delay(100)
                smoke.ndx++
                game.invalidate()
            }
            game.deleteSprite(smoke)
        }
    }


}