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
    sprite = BasicSprite(R.drawable.teleport_ninja,x,y,0),
    game = game,
    basicAnimationSequence = listOf(0),
    speed = 0.06f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(12,13,14,15),
    topAnimationSequence = listOf(6,7),
    bottomAnimationSequence = listOf(2,3),
    rightAnimationSequence = listOf(8,9,10,11),
    target = game.controllableCharacter!!,
    animationDelay = 100L
){
    var chasing = false

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
                        target!!.healthDown(0.25f, 0.2f, currentDirection)
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

