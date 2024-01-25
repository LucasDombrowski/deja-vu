package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


open class Character(val sprite: BasicSprite,
                     val game: Game,
                     val speed:Float,
                     val damages: Float,
                     var hearts: MutableList<Heart>,
                     val basicAnimationSequence: List<Int>,
                     val leftAnimationSequence: List<Int> = basicAnimationSequence,
                     val topAnimationSequence: List<Int> = basicAnimationSequence,
                     val rightAnimationSequence: List<Int> = basicAnimationSequence,
                     val bottomAnimationSequence : List<Int> = basicAnimationSequence,
                     var target : Character? = null){
    private var animation : Job = GlobalScope.launch {
        return@launch
    }
    private var movingAction : Job = GlobalScope.launch {
        return@launch
    }

    var previousDirection = "static"
    var currentDirection = "static"
    fun changePos(x: Float, y: Float){
        sprite.x = x
        sprite.y = y
        game.invalidate()
    }

    fun changeFrame(n: Int){
        sprite.ndx = n
        game.invalidate()
    }

    fun maxAnimationSequence() : Int{
        return listOf<Int>(basicAnimationSequence.size, leftAnimationSequence.size, topAnimationSequence.size, bottomAnimationSequence.size, rightAnimationSequence.size).max()
    }
    fun changeAnimationLoop(animationSequence: List<Int>, awaitTime: Long = 100){
        animation.cancel()
        animation = setInterval(0, maxAnimationSequence()*awaitTime){
            playAnimation(animationSequence, awaitTime)
        }
    }

    fun resetAnimationLoop(awaitTime: Long = 100){
        animation.cancel()
        animation = setInterval(0,maxAnimationSequence()*awaitTime){
            playAnimation(basicAnimationSequence)
        }
    }


    fun playAnimation(frames: List<Int>, awaitTime:Long = 100){
        GlobalScope.launch {
            for (frame in frames){
                changeFrame(frame)
                delay(awaitTime)
            }
        }
    }

    fun moveTo(x: Float, y:Float){
            previousDirection = currentDirection
            movingAction.cancel()
            movingAction = GlobalScope.launch {
                if(x!=sprite.x || y!=sprite.y){
                    val xDifference = when{
                        abs(x-sprite.x)>=speed->speed
                        else->abs(x-sprite.x)%speed
                    }
                    val yDifference = when{
                        abs(y-sprite.y)>=speed->speed
                        else->abs(y-sprite.y)%speed
                    }
                    val nextX = when{
                        x<sprite.x->sprite.x-xDifference
                        x>sprite.x->sprite.x+xDifference
                        else->sprite.x
                    }
                    val nextY = when{
                        y<sprite.y->sprite.y-yDifference
                        y>sprite.y->sprite.y+yDifference
                        else->sprite.y
                    }
                    currentDirection = when{
                        nextX<sprite.x && nextY == sprite.y-> "left"
                        nextX>sprite.x&&nextY==sprite.y-> "right"
                        nextX<sprite.x&&nextY<sprite.y-> "left"
                        nextX<sprite.x&&nextY>sprite.y-> "left"
                        nextX>sprite.x&&nextY<sprite.y-> "right"
                        nextX>sprite.x&&nextY>sprite.y-> "right"
                        nextX==sprite.x&&nextY<sprite.y-> "top"
                        else-> "bottom"
                    }
                    changePos(nextX,nextY)
                    delay(33)
                    checkDirectionChange()
                    moveTo(x,y)
                } else {
                    currentDirection = "static"
                    checkDirectionChange()
                }

            }

    }

    fun inBoundingBox(x: Float, y:Float) : Boolean{
        return (sprite.boundingBox.left < x &&
                sprite.boundingBox.right > x &&
                sprite.boundingBox.top < y &&
                sprite.boundingBox.bottom > y)
    }

    private fun checkDirectionChange(){
        if(previousDirection!=currentDirection){
            val animationSequence = when(currentDirection){
                "left"->leftAnimationSequence
                "right"->rightAnimationSequence
                "top"->topAnimationSequence
                "bottom"->bottomAnimationSequence
                else->basicAnimationSequence
            }
            changeAnimationLoop(animationSequence)
        }
    }

}




