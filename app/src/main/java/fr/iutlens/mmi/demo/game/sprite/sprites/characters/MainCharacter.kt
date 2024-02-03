package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainCharacter(x: Float, y:Float, game: Game) : Character(
    sprite =  BasicSprite(R.drawable.chrono,x,y,2),
    game = game,
    basicAnimationSequence = listOf(2),
    speed = 0.1f,
    invulnerability = 750,
    hearts = setBasicHearts(3),
    leftAnimationSequence = listOf(18,19,20,21,22,23),
    topAnimationSequence = listOf(6,7,8,9,10,11),
    rightAnimationSequence = listOf(12,13,14,15,16,17),
    bottomAnimationSequence = listOf(0,1,2,3,4,5),
    fireRate = 500
    ){

    val targetIndicator : BasicSprite = BasicSprite(R.drawable.arrow, sprite.x, sprite.y)

    val directProjectileBehaviors : MutableList<()->Unit> = mutableListOf()

    val projectile : Projectile = Projectile(BasicSprite(R.drawable.tear, sprite.x, sprite.y), range = 4f, speed = 0.1f, friendly = true, damages =  1f, knockback = 0.5f)

    val items : MutableList<Item> = mutableListOf()

    var autoFire : Job = setInterval(0,fireRate){
        fireToTarget()
    }

    var targetFollow : Job ?= null


    var movingBehavior : (x: Float, y:Float)->Unit = {
        x,y->moveTo(x,y)
    }

    override fun changePos(x: Float, y: Float){
        if(game.map.inForbiddenArea(x,y)){
            movingAction.cancel()
            currentDirection = "static"
            currentAnimationSequence = basicAnimationSequence
            resetAnimationSequence()
        } else if(game.map.inOpenDoor(x,y)){
            movingAction.cancel()
            currentDirection = "static"
            currentAnimationSequence = basicAnimationSequence
            resetAnimationSequence()
            game.nextRoom()
        } else {
            sprite.x = x
            sprite.y = y
        }
        game.invalidate()
    }
    fun fitToFireRate(){
        autoFire.cancel()
        autoFire = setInterval(0,fireRate) {
            fireToTarget()
        }
    }

    fun fireToTarget(){
        if (target is Enemy && target!!.alive && !target!!.sprite.isInvisible()) {
            targetIndicator.visible()
            projectile.aimTarget(target!!, sprite.x, sprite.y)
            with(directProjectileBehaviors.iterator()) {
                forEach {
                    it()
                }
            }
        } else {
            targetIndicator.invisible()
            getClosestEnemy()
        }
    }

    fun refreshHeathBar(){
        val newHearts : MutableList<Heart> = mutableListOf()
        for(heart in hearts){
            newHearts.add(heart.copy())
        }
        game.ath["hearts"] = newHearts
    }

    fun blink(){
        GlobalScope.launch {
            if(remainingInvulnerability){
                delay(100)
                sprite.invisible()
                delay(100)
                sprite.visible()
                blink()
            }
        }
    }

    fun setupTargetFollow(){
        targetFollow = setInterval(0, 33){
            if(target is Enemy){
                targetIndicator.x = target!!.sprite.x
                targetIndicator.y = target!!.sprite.boundingBox.top
            }
        }
    }

    fun getClosestEnemy(){
        val distances : MutableMap<Float,Enemy> = mutableMapOf<Float,Enemy>()
        if(game.map.currentRoom().enemyList!=null) {
            with(game.map.currentRoom().enemyList!!.iterator()) {
                forEach {
                    distances[getDistance(sprite.x, sprite.y, it.sprite.x, it.sprite.y)] = it
                }
            }
            if (distances.isEmpty()) {
                target = null
            } else {
                target = distances.toSortedMap().values.toList().first()
                setupTargetFollow()
            }
        } else {
            target = null
        }
    }



}