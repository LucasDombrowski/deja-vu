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
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainCharacter(x: Float, y:Float, game: Game) : Character(
    sprite =  BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 20f,
    invulnerability = 750,
    hearts = setBasicHearts(3),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    rightAnimationSequence = listOf(6,7,8),
    bottomAnimationSequence = listOf(0,1,2),
    fireRate = 1000
    ){

    val targetIndicator : BasicSprite = BasicSprite(R.drawable.arrow, sprite.x, sprite.y)

    val directProjectileBehaviors : MutableList<()->Unit> = mutableListOf()

    val projectile : Projectile = Projectile(BasicSprite(R.drawable.tear, sprite.x, sprite.y), range = 1000f, speed = 20f, friendly = true, damages =  3, knockback = 15f)

    val items : MutableList<Item> = mutableListOf()

    var autoFire : Job = setInterval(0,fireRate){
        if(target is Enemy && target!!.alive && !target!!.sprite.isInvisible()){
            targetIndicator.visible()
            projectile.aimTarget(target!!, sprite.x, sprite.y)
            with(directProjectileBehaviors.iterator()){
                forEach {
                    it()
                }
            }
        } else {
            targetIndicator.invisible()
        }
    }

    val targetFollow : Job = setInterval(0, 33){
        if(target is Enemy){
            targetIndicator.x = target!!.sprite.x
            targetIndicator.y = target!!.sprite.boundingBox.top
        }
    }

    var movingBehavior : (x: Float, y:Float)->Unit = {
        x,y->moveTo(x,y)
    }

    override fun changePos(x: Float, y: Float){
        if(game.map.inForbiddenArea(x,y)){
            movingAction.cancel()
            currentDirection = "static"
            currentAnimationSequence = basicAnimationSequence
        } else if(game.map.inOpenDoor(x,y)){
            movingAction.cancel()
            game.nextRoom()
        } else {
            sprite.x = x
            sprite.y = y
            game.invalidate()
        }
    }
    fun fitToFireRate(){
        autoFire.cancel()
        Log.i("Firerate","$fireRate")
        autoFire = setInterval(0,fireRate) {
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
        }
    }

    fun refreshHeathBar(){
        val newHearts : MutableList<Heart> = mutableListOf()
        for(heart in hearts){
            Log.i("Heart","${heart.filled}")
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



}