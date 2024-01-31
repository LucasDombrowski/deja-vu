package fr.iutlens.mmi.demo.game.sprite.sprites

import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class Enemy(
    sprite: BasicSprite,
    game: Game,
    speed:Float,
    hearts: MutableList<Heart>,
    basicAnimationSequence: List<Int>,
    leftAnimationSequence: List<Int> = basicAnimationSequence,
    topAnimationSequence: List<Int> = basicAnimationSequence,
    rightAnimationSequence: List<Int> = basicAnimationSequence,
    bottomAnimationSequence : List<Int> = basicAnimationSequence,
    target : Character? = null,
    fireRate : Long = 0,
    open var action : Job = GlobalScope.launch {
        return@launch
    }
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
    fun hit(damages: Int, knockback: Float, direction: String){
        healthDown(damages, knockback, direction)
        GlobalScope.launch {
            sprite.semiRedColor()
            delay(100)
            sprite.normalColor()
        }
    }


}