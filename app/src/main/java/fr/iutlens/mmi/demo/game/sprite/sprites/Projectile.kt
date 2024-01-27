package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import androidx.compose.ui.graphics.Canvas
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Projectile(var sprite: BasicSprite, val friendly : Boolean = false, var speed: Float, var range: Float, var damages: Float, var knockback : Float) {

    fun changePos(x: Float, y:Float){
        sprite.x = x
        sprite.y = y
    }
    fun setup(game: Game, xStart: Float, yStart: Float) : Projectile{
        val newProjectile = copy()
        newProjectile.changePos(xStart,yStart)
        game.addSprite(newProjectile.sprite)
        return newProjectile
    }

    fun aimTarget(target : Character, xStart: Float, yStart: Float){
        fireProjectile(target.game, xStart, yStart, target.sprite.x, target.sprite.y)
    }
    fun fireProjectile(game: Game, xStart: Float, yStart: Float, x: Float, y: Float){
        val newProjectile = setup(game, xStart, yStart = yStart)
        val steps = newProjectile.calculateSteps(x,y)
        val xStep = when{
            x<xStart->-steps[0]
            else->steps[0]
        }
        val yStep = when{
            y<yStart->-steps[1]
            else->steps[1]
        }
        newProjectile.moveProjectile(xStep,yStep,game)

    }

    fun moveProjectile(xStep: Float, yStep: Float, game: Game){
        GlobalScope.launch {
            repeat(round(range/speed).toInt()){
                delay(33)
                changePos(sprite.x+xStep, sprite.y+yStep)
                for(character in game.characterList){
                    if(character.inBoundingBox(sprite.x, sprite.y)){
                        if(friendly && character is Enemy){
                            this.cancel()
                            val direction = when{
                                sprite.x < character.sprite.x -> "right"
                                sprite.x > character.sprite.x -> "left"
                                sprite.y < character.sprite.y -> "bottom"
                                else -> "top"
                            }
                            game.deleteSprite(sprite)
                            character.hit(damages,knockback,direction)
                        }
                    }
                }
            }
            game.deleteSprite(sprite)
        }
    }

    fun calculateSteps(x : Float, y : Float) : List<Float> {
        val vectorX = abs( round(x) - round(sprite.x))
        val vectorY = abs(round(y) - round(sprite.y))
        return if (vectorX == 0f && vectorY == 0f) {
            listOf(0f, 0f)
        } else if (vectorX == 0f) {
            listOf(0f, speed)
        } else if(vectorY==0f){
            listOf(speed,0f)
        } else if(vectorX==vectorY){
            listOf(speed,speed)
        } else if(vectorX>vectorY){
            listOf(speed,speed/(vectorX/vectorY))
        } else {
            listOf(speed/(vectorY/vectorX),speed)
        }
    }

    fun copy() : Projectile{
        return Projectile(
            sprite = sprite.copy(),
            friendly = friendly,
            speed = speed,
            range = range,
            damages = damages,
            knockback = knockback
        )
    }

}