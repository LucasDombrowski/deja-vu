package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round


open class Character(val sprite: BasicSprite,
                     val game: Game,
                     var speed:Float,
                     var hearts: MutableList<Heart>,
                     var fireRate : Long = 0,
                     val invulnerability: Long = 0,
                     val basicAnimationSequence: List<Int>,
                     val leftAnimationSequence: List<Int> = basicAnimationSequence,
                     val topAnimationSequence: List<Int> = basicAnimationSequence,
                     val rightAnimationSequence: List<Int> = basicAnimationSequence,
                     val bottomAnimationSequence : List<Int> = basicAnimationSequence,
                     var target : Character? = null){


    var movingAction : Job = GlobalScope.launch {
        return@launch
    }

    var alive = true

    var currentAnimationSequence = basicAnimationSequence

    val animation : Job = setInterval(0,maxAnimationSequence().toLong()*100){
        playAnimation(currentAnimationSequence)
    }

    var remainingInvulnerability : Boolean = false

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
                if(round(x)!= round(sprite.x) || round(y)!=round(sprite.y)){
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
                    if(previousDirection!=currentDirection){
                        currentAnimationSequence = when(currentDirection){
                            "left"->leftAnimationSequence
                            "right"->rightAnimationSequence
                            "top"->topAnimationSequence
                            "bottom"->bottomAnimationSequence
                            else->basicAnimationSequence
                        }
                    }
                    changePos(nextX,nextY)
                    delay(33)
                    moveTo(x,y)
                } else {
                    currentDirection = "static"
                    currentAnimationSequence = basicAnimationSequence
                }

            }

    }

    fun inBoundingBox(x: Float, y:Float) : Boolean{
        return (sprite.boundingBox.left < x &&
                sprite.boundingBox.right > x &&
                sprite.boundingBox.top < y &&
                sprite.boundingBox.bottom > y)
    }


    fun healthDown(n: Int, knockback: Float = 0f, direction: String = "static"){
        if(!remainingInvulnerability) {
            if(invulnerability>0){
                remainingInvulnerability = true
                decreaseInvulnerability()
            }
            var healthToRemove = n
            var heartIndex = hearts.lastIndex
            while(healthToRemove>0 && heartIndex>=0){
                while (hearts[heartIndex].filled>0f && healthToRemove>0){
                    hearts[heartIndex].filled-=1
                    healthToRemove-=1
                }
                if(!hearts[heartIndex].permanent && hearts[heartIndex].filled<=0){
                    hearts.removeAt(heartIndex)
                }
                heartIndex--
            }
            getKnockback(knockback,direction)
            if(this is MainCharacter){
                refreshHeathBar()
                blink()
            } else if(hearts[0].filled<=0){
                die()
            }
        }
    }

    fun getKnockback(knockback: Float, direction: String){
        movingAction.cancel()
        val xMultiplier= when(direction){
            "left"->-1
            "right"->1
            else->0
        }
        val yMultiplier = when(direction){
            "top"->-1
            "bottom"->1
            "left"->0
            "right"->0
            else->1
        }
        GlobalScope.launch {
            repeat(10){
                changePos(sprite.x+(knockback*xMultiplier), sprite.y + (knockback*yMultiplier))
                delay(33)
            }
        }
    }

    fun copy() : Character{
        return Character(
            sprite.copy(), game, speed, hearts, fireRate, invulnerability, basicAnimationSequence, leftAnimationSequence, topAnimationSequence
        )
    }

    fun getReverseDirection() : String{
        return when(currentDirection){
            "left"->"right"
            "right"->"left"
            "top"->"bottom"
            "bottom"->"top"
            else->"static"
        }
    }
    fun decreaseInvulnerability(){
        GlobalScope.launch {
            delay(invulnerability)
            remainingInvulnerability = false
        }
    }

    fun die(){
        alive = false
        game.deleteSprite(sprite)
        game.deleteCharacter(character = this)
        movingAction.cancel()
        if(this is Enemy){
            action.cancel()
        }
    }

}




