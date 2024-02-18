package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import androidx.compose.ui.geometry.Offset
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
    hearts = setBasicHearts(5),
    leftAnimationSequence = listOf(18,19,20,21,22,23),
    topAnimationSequence = listOf(6,7,8,9,10,11),
    rightAnimationSequence = listOf(12,13,14,15,16,17),
    bottomAnimationSequence = listOf(0,1,2,3,4,5),
    fireRate = 500
    ){

    val targetIndicator : BasicSprite = BasicSprite(R.drawable.arrow, sprite.x, sprite.y)

    var directProjectileBehaviors : MutableList<()->Unit> = mutableListOf()

    var projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,4), range = 4f, speed = 0.1f, friendly = true, damages =  1f, knockback = 0.5f)

    var items : MutableList<Item> = mutableListOf()

    var autoFire : Job = setInterval(0,fireRate){
        GlobalScope.launch {
            while (game.pause){
                delay(fireRate)
            }
            fireToTarget()
        }
    }

    var temporaryMovingInteraction : (x: Float, y: Float) -> Unit = {
        x, y ->  
    }

    var targetFollow : Job ?= null


    var tapMovingBehavior : (x: Float, y:Float)->Unit = {
        x,y->
        setupPath(x,y)
    }

    var dragMovingBehavior : (x: Float, y:Float)->Unit = {
            x,y->
            disablePathFollowing()
            moveTo(x,y)
    }

    override fun changePos(x: Float, y: Float){
        if(game.map.inForbiddenArea(
                x,
                y + (sprite.boundingBox.bottom - sprite.boundingBox.top)/3
        )){
            GlobalScope.launch {
                stun()
                delay(33)
                restart()
            }
        } else if(game.map.inOpenDoor(x,y) && game.map.currentRoom().open){
            game.map.currentRoom().close()
            stun()
            game.map.nextRoom().placeCharacter(game)
            game.nextRoom()
        } else {
            temporaryMovingInteraction(x,y)
            sprite.x = x
            sprite.y = y
        }
        game.invalidate()
    }
    fun fitToFireRate(){
        autoFire.cancel()
        autoFire = setInterval(0,fireRate) {
            GlobalScope.launch {
                while (game.pause){
                    delay(fireRate)
                }
                fireToTarget()
            }

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
        }
        game.invalidate()
    }

    fun refreshHeathBar(){
        val newHearts : MutableList<Heart> = mutableListOf()
        for(heart in hearts){
            newHearts.add(heart.copy())
        }
        newHearts.sortBy {
            it.permanent
        }
        hearts = newHearts
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
            GlobalScope.launch {
                while (game.pause){
                    delay(33)
                }
                if(target is Enemy){
                    targetIndicator.x = target!!.sprite.x
                    targetIndicator.y = target!!.sprite.boundingBox.top
                }
            }

        }
    }

    fun getClosestEnemy(){
        val distances : MutableMap<Float,Enemy> = mutableMapOf<Float,Enemy>()
        with(game.characterList.iterator()) {
            forEach {
                if(it is Enemy) {
                    distances[getDistance(sprite.x, sprite.y, it.sprite.x, it.sprite.y)] = it
                }
            }
        }
        if (distances.isEmpty()) {
            target = null
        } else {
            target = distances.toSortedMap().values.toList().first()
            setupTargetFollow()
        }
    }

    fun changeProjectileSkin(ndx: Int, animation : (Projectile)->Job =
        {
        projectile->GlobalScope.launch {
            return@launch
        }
        }
    ){
        projectile.sprite.ndx = ndx
        projectile.animation = animation
    }



}