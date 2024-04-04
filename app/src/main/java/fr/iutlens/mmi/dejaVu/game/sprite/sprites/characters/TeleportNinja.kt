package fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Enemy
import fr.iutlens.mmi.dejaVu.utils.Music
import fr.iutlens.mmi.dejaVu.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TeleportNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(4),
    speed = 0.05f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(12,13,14),
    topAnimationSequence = listOf(30,31,32),
    bottomAnimationSequence = listOf(3,4,5),
    rightAnimationSequence = listOf(21,22,23),
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
        val soundVolume = 0.125f
        disablePathFollowing()
        if(!game.ended) {
            GlobalScope.launch {
                while (game.pause) {
                    delay(33)
                }
                if (alive) {
                    if (!chasing) {
                        targetable = false
                        Music.playSound(R.raw.teleport, leftVolume = soundVolume, rightVolume = soundVolume)
                        sprite.setTransparencyLevel(0.75f)
                        delay(33)
                        sprite.setTransparencyLevel(0.5f)
                        delay(33)
                        sprite.setTransparencyLevel(0.25f)
                        delay(33)
                        sprite.invisible()
                        delay(2000)
                        var xPos = when (Math.random()) {
                            in 0f..0.5f -> target!!.sprite.x - game.map.tileArea.w
                            else -> target!!.sprite.x + game.map.tileArea.w
                        }
                        var yPos = when (Math.random()) {
                            in 0f..0.5f -> target!!.sprite.y + game.map.tileArea.h
                            else -> target!!.sprite.y - game.map.tileArea.w
                        }
                        while (game.map.inForbiddenArea(xPos,yPos)){
                            xPos = when (Math.random()) {
                                in 0f..0.5f -> target!!.sprite.x - game.map.tileArea.w
                                else -> target!!.sprite.x + game.map.tileArea.w
                            }
                            yPos = when (Math.random()) {
                                in 0f..0.5f -> target!!.sprite.y + game.map.tileArea.h
                                else -> target!!.sprite.y - game.map.tileArea.w
                            }
                        }
                        chasing = true
                        changePos(xPos, yPos)
                        Music.playSound(R.raw.teleport, leftVolume = soundVolume, rightVolume = soundVolume)
                        sprite.visible()
                        sprite.setTransparencyLevel(0.25f)
                        delay(15)
                        sprite.setTransparencyLevel(0.5f)
                        delay(15)
                        sprite.setTransparencyLevel(0.75f)
                        delay(15)
                        sprite.setTransparencyLevel(1f)
                        targetable = true
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
                        if (isPathFree(target!!.sprite.x, target!!.sprite.y)) {
                            moveTo(target!!.sprite.x, target!!.sprite.y)
                            action = GlobalScope.launch {
                                delay(100)
                                pattern()
                            }
                        } else {
                            followPlayer()
                        }
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

    fun followPlayer(){
        action.cancel()
        setupPath(target!!.sprite.x, target!!.sprite.y)
        pathFollow = true
        action = setInterval(0,100){
            if(isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow){
                action.cancel()
                pattern()
            }
        }
    }

}

