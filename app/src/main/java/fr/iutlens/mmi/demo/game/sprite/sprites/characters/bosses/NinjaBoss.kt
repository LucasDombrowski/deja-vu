package fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses

import android.util.Log
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Boss
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.getCenter
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.rotationFromPoint
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI

class NinjaBoss(x: Float, y: Float, game: Game) : Boss(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(20),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
) {

    var countdown : Job? = null
    val projectile : Projectile = Projectile(BasicSprite(R.drawable.tear, sprite.x, sprite.y), range = 4f, speed = 0.1f, friendly = false, damages =  0.5f, knockback = 0.2f)
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
        action.cancel()
        if(countdown!=null) {
            countdown!!.cancel()
        }
        var newPattern = (1..5).random()
        while (newPattern==pattern){
            newPattern = (1..5).random()
        }
        pattern = newPattern
        GlobalScope.launch {
            delay(1000)
            when(pattern){
                1->chasePlayer()
                2->aimPlayer()
                3->teleportToPlayer()
                4->reflectShots()
                else->spawnEnemies()
            }
        }

    }
    fun chasePlayer(){
        action = setInterval(0,33){
            moveTo(target!!.sprite.x, target!!.sprite.y)
            if(target!!.inBoundingBox(sprite.x, sprite.y)){
                target!!.healthDown(1f, 0.2f, currentDirection)
                randomPattern()
            }
        }
        patternCountdown()
    }

    fun aimPlayer(){
        if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)>projectile.realRange(game)){
            action = setInterval(0,33){
                moveTo(target!!.sprite.x, target!!.sprite.y)
                if(getDistance(sprite.x, sprite.y, target!!.sprite.x, target!!.sprite.y)<projectile.realRange(game)){
                    action.cancel()
                    aimPlayer()
                }
            }
        } else {
            action = GlobalScope.launch {
                repeat(3){
                    val center = getCenter(target!!.sprite.x, target!!.sprite.y, sprite.x, sprite.y)
                    val rotationPoints = listOf(
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (PI/6).toFloat()),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (PI/3).toFloat()),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (-PI/6).toFloat()),
                        rotationFromPoint(target!!.sprite.x, target!!.sprite.y, center[0], center[1], (-PI/3).toFloat())
                    )
                    projectile.aimTarget(target!!, sprite.x, sprite.y)
                    for(rotationPoint in rotationPoints){
                        projectile.fireProjectile(game, sprite.x, sprite.y, rotationPoint[0], rotationPoint[1])
                    }
                    delay(1000)
                }
                randomPattern()
            }
        }
    }

    fun teleportToPlayer(){
        GlobalScope.launch {
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
            changePos(xPos, yPos)
            sprite.visible()
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
        game.map.currentRoom().spawnEnemies((1..3))
        randomPattern()
    }


}