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
        basicAnimationSequence = when{
            previousDirection == "left"-> listOf(4)
            previousDirection== "right"-> listOf(0)
            target!=null&&target!!.sprite.x<sprite.x-> listOf(0)
            target!=null&&target!!.sprite.x>sprite.x-> listOf(4)
            else->basicAnimationSequence
        }
        return basicAnimationSequence
    }
    override fun spawn(x: Float, y: Float){
        game.addCharacter(this)
        changePos(x, y)
        GlobalScope.launch {
            delay(1000)
            invisible()
        }
    }

    fun chasePlayer(){
        action.cancel();
        action = setInterval(0,100){
            if (target!!.inBoundingBox(sprite.x, sprite.y)){
                target!!.healthDown(0.25f, 0.2f, currentDirection)
                chasing = false
                invisible();
            } else {
                if (isPathFree(target!!.sprite.x, target!!.sprite.y)) {
                    moveTo(target!!.sprite.x, target!!.sprite.y)
                } else {
                    followPlayer()
                }
            }
        }
    }

    fun invisible(){
        action.cancel()
        val soundVolume = 0.125f
        if(alive){
            action = GlobalScope.launch {
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
                teleport()
            }
        }
    }

    fun teleport(){
        var xPos = when (Math.random()) {
            in 0f..0.5f -> target!!.sprite.x - game.map.tileArea.w*2
            else -> target!!.sprite.x + game.map.tileArea.w*2
        }
        var yPos = when (Math.random()) {
            in 0f..0.5f -> target!!.sprite.y + game.map.tileArea.h*2
            else -> target!!.sprite.y - game.map.tileArea.h*2
        }
        while (game.map.inForbiddenArea(xPos,yPos)){
            xPos = when (Math.random()) {
                in 0f..0.5f -> target!!.sprite.x - game.map.tileArea.w*2
                else -> target!!.sprite.x + game.map.tileArea.w*2
            }
            yPos = when (Math.random()) {
                in 0f..0.5f -> target!!.sprite.y + game.map.tileArea.h*2
                else -> target!!.sprite.y - game.map.tileArea.h*2
            }
        }
        chasing = true
        changePos(xPos, yPos)
        visible()
    }

    fun visible(){
        val soundVolume = 0.125f
        action.cancel()
        if(alive){
            action = GlobalScope.launch {
                if(game.pause){
                    delay(100)
                    visible()
                } else {
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
                    chasePlayer()
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
                chasePlayer()
            }
        }
    }

}

