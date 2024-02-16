package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.utils.getAngle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

class Projectile(var sprite: BasicSprite, var friendly : Boolean = false, var speed: Float, var range: Float, var damages: Float, var knockback : Float, var aoe : Boolean = false, var onHitEffects : MutableList<(character : Character)->Unit> = mutableListOf()) {

    fun realSpeed(game: Game) : Float{
        return speed*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }

    fun realRange(game: Game) : Float{
        return range*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }
    fun changePos(x: Float, y:Float){
        sprite.x = x
        sprite.y = y
    }

    var animation : (Projectile)->Job =
        {
        projectile ->  GlobalScope.launch {
            return@launch
        }
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
        val steps = newProjectile.calculateSteps(x,y, game)
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
        sprite.rotate = -getAngle(sprite.x, sprite.y, sprite.x+xStep, sprite.y+yStep)
        val spriteAnimation = animation(this)
        GlobalScope.launch {
            var contact = false
            repeat(round(realRange(game)/realSpeed(game)).toInt()){
                while (game.pause){
                    delay(33)
                }
                delay(33)
                changePos(sprite.x+xStep, sprite.y+yStep)
                if(friendly){
                    with(game.copyCharacterList().iterator()){
                        forEach {
                            if (it.inBoundingBox(sprite.x, sprite.y) && it is Enemy && it.alive) {
                                val direction = when {
                                    sprite.x < it.sprite.x -> "right"
                                    sprite.x > it.sprite.x -> "left"
                                    sprite.y < it.sprite.y -> "bottom"
                                    else -> "top"
                                }
                                spriteAnimation.cancel()
                                game.deleteSprite(sprite)
                                if(!aoe && !contact) {
                                    contact = true
                                    if(!it.reflect) {
                                        it.hit(damages, knockback, direction)
                                        val character = it
                                        with(onHitEffects.iterator()) {
                                            forEach {
                                                it(character)
                                            }
                                        }
                                    } else {
                                        friendly = !friendly
                                        copy().fireProjectile(game, it.sprite.x, it.sprite.y, it.sprite.x - xStep, it.sprite.y - yStep)
                                    }
                                }
                                cancel()
                            }
                        }
                    }
                } else {
                    if(game.controllableCharacter!!.inBoundingBox(sprite.x, sprite.y)){
                        val direction = when{
                            sprite.x < game.controllableCharacter!!.sprite.x -> "right"
                            sprite.x > game.controllableCharacter!!.sprite.x -> "left"
                            sprite.y < game.controllableCharacter!!.sprite.y -> "bottom"
                            else -> "top"
                        }
                        spriteAnimation.cancel()
                        game.deleteSprite(sprite)
                        game.controllableCharacter!!.healthDown(damages, knockback, direction)
                        cancel()
                    }
                }
                if(game.map.inForbiddenArea(sprite.x, sprite.y)){
                    spriteAnimation.cancel()
                    game.deleteSprite(sprite)
                    cancel()
                }
                game.invalidate()
            }
            spriteAnimation.cancel()
            game.deleteSprite(sprite)
        }
    }

    fun calculateSteps(x : Float, y : Float, game : Game) : List<Float> {
        val vectorX = abs( round(x) - round(sprite.x))
        val vectorY = abs(round(y) - round(sprite.y))
        return if (vectorX == 0f && vectorY == 0f) {
            listOf(0f, 0f)
        } else if (vectorX == 0f) {
            listOf(0f, realSpeed(game))
        } else if(vectorY==0f){
            listOf(realSpeed(game),0f)
        } else if(vectorX==vectorY){
            listOf(realSpeed(game),realSpeed(game))
        } else if(vectorX>vectorY){
            listOf(realSpeed(game),realSpeed(game)/(vectorX/vectorY))
        } else {
            listOf(realSpeed(game)/(vectorY/vectorX),realSpeed(game))
        }
    }

    fun copy() : Projectile{
        val newProjectile = Projectile(
            sprite = sprite.copyReset(),
            friendly = friendly,
            speed = speed,
            range = range,
            damages = damages,
            knockback = knockback,
        )
        newProjectile.animation = animation
        return newProjectile
    }

}