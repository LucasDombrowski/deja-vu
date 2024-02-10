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


open class Character(
    var sprite: BasicSprite,
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
    val animationDelay : Long = 100,
    var target : Character? = null,
    var reflect : Boolean = false){


    var movingAction : Job = GlobalScope.launch {
        return@launch
    }
    var alive = true
    var mobile = true
    var remainingInvulnerability : Boolean = false
    var animationTick = 0
    var previousDirection = "static"
    var currentDirection = "static"
    var currentAnimationSequence = basicAnimationSequence
    var currentAnimationSequenceIndex : Int = 0
    val characterAnimation : Job = setInterval(0, animationDelay){
        if(currentAnimationSequenceIndex>=currentAnimationSequence.size-1){
            currentAnimationSequenceIndex = 0
        } else {
            currentAnimationSequenceIndex ++
        }
        sprite.ndx = currentAnimationSequence[currentAnimationSequenceIndex]
    }
    open fun changePos(x: Float, y: Float){
        if(game.map.inForbiddenArea(
            x,
            y
        )){
            GlobalScope.launch {
                stun()
                delay(33)
                restart()
            }
        } else if(!game.contactWithOtherCharacter(this,x,y)){
            sprite.x = x
            sprite.y = y
        } else {
            getKnockback(0.2f, currentDirection)
        }
        game.invalidate()
    }


    fun realSpeed() : Float{
        return speed*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }

    fun realKnockback(knockback: Float) : Float{
        return knockback*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }
    fun changeFrame(n: Int){
        sprite.ndx = n
        game.invalidate()
    }

    fun moveTo(x: Float, y:Float){
        if(mobile){
            if((this==game.controllableCharacter!! && !game.movingRestriction) || this!=game.controllableCharacter!!) {
                previousDirection = currentDirection
                movingAction.cancel()
                movingAction = GlobalScope.launch {
                    if (round(x) != round(sprite.x) || round(y) != round(sprite.y)) {
                        val xDifference = when {
                            abs(x - sprite.x) >= realSpeed() -> realSpeed()
                            else -> abs(x - sprite.x) % realSpeed()
                        }
                        val yDifference = when {
                            abs(y - sprite.y) >= realSpeed() -> realSpeed()
                            else -> abs(y - sprite.y) % realSpeed()
                        }
                        val nextX = when {
                            x < sprite.x -> sprite.x - xDifference
                            x > sprite.x -> sprite.x + xDifference
                            else -> sprite.x
                        }
                        val nextY = when {
                            y < sprite.y -> sprite.y - yDifference
                            y > sprite.y -> sprite.y + yDifference
                            else -> sprite.y
                        }
                        currentDirection = when {
                            nextX < sprite.x && nextY == sprite.y -> "left"
                            nextX > sprite.x && nextY == sprite.y -> "right"
                            nextX < sprite.x && nextY < sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "top"
                            } else {
                                "left"
                            }

                            nextX < sprite.x && nextY > sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "bottom"
                            } else {
                                "left"
                            }

                            nextX > sprite.x && nextY < sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "top"
                            } else {
                                "right"
                            }

                            nextX > sprite.x && nextY > sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "bottom"
                            } else {
                                "right"
                            }

                            nextX == sprite.x && nextY < sprite.y -> "top"
                            else -> "bottom"
                        }
                        currentAnimationSequence = when(currentDirection){
                            "left"->leftAnimationSequence
                            "right"->rightAnimationSequence
                            "top"->topAnimationSequence
                            "bottom"->bottomAnimationSequence
                            else->basicAnimationSequence
                        }
                        changePos(nextX, nextY)
                        delay(33)
                        moveTo(x, y)
                    } else {
                        currentDirection = "static"
                        currentAnimationSequence = basicAnimationSequence
                    }
                }
            }
        }

    }

    fun stun(){
        movingAction.cancel()
        mobile = false
        currentDirection = "static"
        currentAnimationSequence = basicAnimationSequence
    }

    fun restart(){
        mobile = true
    }
    fun inBoundingBox(x: Float, y:Float) : Boolean{
        return (sprite.boundingBox.left < x &&
                sprite.boundingBox.right > x &&
                sprite.boundingBox.top < y &&
                sprite.boundingBox.bottom > y)
    }


    fun healthDown(n: Float, knockback: Float = 0f, direction: String = "static"){
        if(!remainingInvulnerability) {
            if(invulnerability>0){
                remainingInvulnerability = true
                decreaseInvulnerability()
            }
            var healthToRemove = n
            var heartIndex = hearts.lastIndex
            while(healthToRemove>0 && heartIndex>=0){
                while (hearts[heartIndex].filled>0f && healthToRemove>0){
                    hearts[heartIndex].filled-=0.25f
                    healthToRemove-=0.25f
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
                changePos(sprite.x+(realKnockback(knockback)/10*xMultiplier), sprite.y + (realKnockback(knockback)/10*yMultiplier))
                delay(33)
            }
        }
    }

    open fun copy() : Character{
        return Character(
            sprite.copy(), game, speed, hearts, fireRate, invulnerability, basicAnimationSequence, leftAnimationSequence, topAnimationSequence, bottomAnimationSequence, target = target
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
        if(this is Enemy && this !is Boss){
            action.cancel()
            game.map.currentRoom().enemyList!!.remove(this)
            game.map.currentRoom().checkEnemyList()
        }
        if(this is Enemy && this is Boss){
            game.onEnd()
            action.cancel()
        }
        if(this==game.controllableCharacter!!.target){
            game.controllableCharacter!!.getClosestEnemy()
        }
        game.invalidate()
    }

    init {

    }

}




