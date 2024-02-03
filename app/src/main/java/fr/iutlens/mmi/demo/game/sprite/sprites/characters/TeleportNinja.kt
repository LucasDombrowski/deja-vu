package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class TeleportNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
){
    var chasing = false
    override fun spawn(x: Float, y: Float){
        game.addCharacter(this)
        changePos(x, y)
        action = GlobalScope.launch {
            delay(1000)
            pattern()
        }
    }
    fun pattern() {
        GlobalScope.launch {
            if(alive) {
                if (!chasing) {
                    sprite.invisible()
                    val xPos = when (Math.random()) {
                        in 0f..0.5f -> target!!.sprite.x - 50f
                        else -> target!!.sprite.x + 50f
                    }
                    val yPos = when (Math.random()) {
                        in 0f..0.5f -> target!!.sprite.y + 50f
                        else -> target!!.sprite.y - 50f
                    }
                    chasing = true
                    delay(2000)
                    changePos(xPos, yPos)
                    sprite.visible()
                    action = GlobalScope.launch {
                        pattern()
                    }

                } else if (target!!.inBoundingBox(sprite.x, sprite.y)) {
                    target!!.healthDown(0.5f, 0.2f, currentDirection)
                    chasing = false
                    action = GlobalScope.launch {
                        delay(100)
                        pattern()
                    }
                } else {
                    moveTo(target!!.sprite.x, target!!.sprite.y)
                    action = GlobalScope.launch {
                        delay(100)
                        pattern()
                    }
                }
            }
        }
    }
    override fun copy() : TeleportNinja{
        val newCharacter = TeleportNinja(sprite.x, sprite.y, game)
        newCharacter.sprite = newCharacter.sprite.copy()
        return newCharacter
    }

}

