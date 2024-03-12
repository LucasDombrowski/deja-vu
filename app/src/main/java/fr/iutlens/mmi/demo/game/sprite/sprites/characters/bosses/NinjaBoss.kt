package fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Boss
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.degreesToRadiant
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NinjaBoss(x: Float, y: Float, game: Game) : Boss(
    sprite = BasicSprite(R.drawable.big_isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(40),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
) {

    var countdown : Job? = null
    val projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,5), range = 4f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f)
    var pattern = 0
    override fun copy() : NinjaBoss{
        return NinjaBoss(sprite.x,sprite.y, game)
    }

    override fun spawn(x: Float, y:Float){
        game.ath["boss"] = hearts
        game.addCharacter(this)
        changePos(x,y)
        randomPattern()
    }

    fun randomPattern(){
        disablePathFollowing()
        action.cancel()
        if(!game.ended) {
            if (alive) {
                if (countdown != null) {
                    countdown!!.cancel()
                }
                var newPattern = (1..5).random()
                while (newPattern == pattern) {
                    newPattern = (1..5).random()
                }
                pattern = newPattern
                GlobalScope.launch {
                    delay(1000)
                    when (pattern) {
                        1 -> chasePlayer()
                        2 -> aimPlayer()
                        3 -> teleportToPlayer()
                        4 -> reflectShots()
                        else -> spawnEnemies()
                    }
                }
            }
        }
    }
    fun chasePlayer(){
        disablePathFollowing()
        action = setInterval(0,33){
            moveTo(target!!.sprite.x, target!!.sprite.y)
            if(target!!.inBoundingBox(sprite.x, sprite.y)){
                target!!.healthDown(1f, 0.2f, currentDirection)
                randomPattern()
            } else if(!isPathFree(target!!.sprite.x, target!!.sprite.y)){
                followPlayer()
            }
        }
        patternCountdown()
    }

    fun followPlayer(){
        action.cancel()
        setupPath(target!!.sprite.x, target!!.sprite.y)
        pathFollow = true
        action = setInterval(0,100){
            if(isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow){
                randomPattern()
            }
        }
    }

    fun aimPlayer(){
        disablePathFollowing()
        if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)>projectile.realRange(game)){
            action = setInterval(0,33){
                moveTo(target!!.sprite.x, target!!.sprite.y)
                if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)<projectile.realRange(game)){
                    action.cancel()
                    aimPlayer()
                } else if(!isPathFree(target!!.sprite.x, target!!.sprite.y)){
                    action.cancel()
                    setupPath(target!!.sprite.x, target!!.sprite.y)
                    pathFollow = true
                    action = setInterval(0,100){
                        if(isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow){
                            randomPattern()
                        }
                    }
                }
            }
        } else {
            action = GlobalScope.launch {
                repeat(3){
                    while(game.pause){
                        delay(33)
                    }
                    val center = getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
                    val rotationPoints = listOf(
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], degreesToRadiant(40f)),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], degreesToRadiant(80f)),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], degreesToRadiant(-40f)),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], degreesToRadiant(-80f))
                    )
                    projectile.aimTarget(target!!, sprite.x, sprite.y)
                    for(rotationPoint in rotationPoints){
                        projectile.fireProjectile(game, sprite.x, sprite.y, rotationPoint[0], rotationPoint[1])
                    }
                    delay(1500)
                }
                randomPattern()
            }
        }
    }

    fun teleportToPlayer(){
        GlobalScope.launch {
            sprite.setTransparencyLevel(0.75f)
            delay(33)
            sprite.setTransparencyLevel(0.5f)
            delay(33)
            sprite.setTransparencyLevel(0.25f)
            delay(33)
            sprite.invisible()
            val xPos = when (Math.random()) {
                in 0f..0.5f -> target!!.sprite.x - 50f
                else -> target!!.sprite.x + 50f
            }
            val yPos = when (Math.random()) {
                in 0f..0.5f -> target!!.sprite.y + 50f
                else -> target!!.sprite.y - 50f
            }
            delay(1000)
            while (game.pause){
                delay(33)
            }
            changePos(xPos, yPos)
            sprite.visible()
            sprite.setTransparencyLevel(0.25f)
            delay(15)
            sprite.setTransparencyLevel(0.5f)
            delay(15)
            sprite.setTransparencyLevel(0.75f)
            delay(15)
            sprite.setTransparencyLevel(1f)
            chasePlayer()
        }

    }
    fun patternCountdown(){
        countdown = GlobalScope.launch {
            delay(5000)
            randomPattern()
        }
    }

    fun reflectShots(){
        reflect = true;
        sprite.colorFilter = ColorFilter.colorMatrix(
            ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        )
        randomPattern()
        GlobalScope.launch {
            delay(5000)
            reflect = false
            sprite.normalColor()
        }
    }

    fun spawnEnemies(){
        if(!game.map.currentRoom().enemiesAlive(game)) {
            game.map.currentRoom().enemyList = mutableListOf()
            game.map.currentRoom().spawnEnemies()
        }
        randomPattern()
    }


}