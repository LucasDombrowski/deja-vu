package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.DrawingSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.Projectile
import fr.iutlens.mmi.demo.utils.getDistance
import fr.iutlens.mmi.demo.utils.setInterval
import fr.iutlens.mmi.demo.utils.vibrate
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

    val targetIndicator : BasicSprite = BasicSprite(R.drawable.target_indicator, sprite.x, sprite.y)

    val pathIndicator : DrawingSprite = DrawingSprite(R.drawable.path_indicator, sprite.x, sprite.y, drawColor = Color(243,214,55,128))

    var dragAction = false

    var previousPathIndicatorTile = game.map.getMapIndexFromPosition(sprite.x, sprite.y)

    var directProjectileBehaviors : MutableList<()->Unit> = mutableListOf()

    var projectile : Projectile = Projectile(BasicSprite(R.drawable.projectiles, sprite.x, sprite.y,4), range = 4f, speed = 0.1f, friendly = true, damages =  1f, knockback = 0.5f)

    var items : MutableList<Item> = mutableListOf(OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart(),OneHeart())

    var autoFire : Job = setInterval(0,fireRate){
        GlobalScope.launch {
            while (game.pause){
                delay(fireRate)
            }
            fireToTarget()
        }
    }

    var moving = false

    var temporaryMovingInteraction : (x: Float, y: Float) -> Unit = {
        x, y ->  
    }

    var targetFollow : Job ?= null

    var dragStartBehavior : (x:Float, y:Float)->Unit = {
        x, y ->
        pathIndicator.drawing = true
        movePathIndicator(x,y,true)


    }

    var dragEndBehavior : ()->Unit = {
        if(dragAction){
            dragAction = false
            erasePathDrawing()
        }
    }


    var tapMovingBehavior : (x: Float, y:Float)->Unit = {
        x,y->
        movePathIndicator(x,y)
    }

    var dragMovingBehavior : (x: Float, y:Float)->Unit = {
            x,y->
            movePathIndicator(x,y)
    }

    var dragStandBehavior : Job = GlobalScope.launch {  }


    fun movePathIndicator(x: Float, y: Float, reset: Boolean = false){
        dragStandBehavior.cancel()
        dragAction = true
        if(mobile) {
            pathIndicator.visible()
            pathIndicator.x = x
            pathIndicator.y = y
            if(reset || pathIndicator.lastPositions.size==0){
                pathIndicator.resetPositions()
            } else {
                pathIndicator.newPosition()
            }
            game.invalidate()
            if(!moving){
                moveToPathIndicator()
            }
        }
        dragStandBehavior = GlobalScope.launch {
            delay(100)
            dragEndBehavior()
        }
    }

    fun erasePathDrawing(){
        if(!dragAction){
            pathIndicator.erase()
        }
    }

    fun pathIndicatorTile() : Pair<Int,Int>{
        return game.map.getMapIndexFromPosition(pathIndicator.x, pathIndicator.y)
    }
    fun inIndicatorBoundingBox(x : Float = sprite.x, y: Float = sprite.y) : Boolean{
        return x in pathIndicator.boundingBox.left..pathIndicator.boundingBox.right && y in pathIndicator.boundingBox.top..pathIndicator.boundingBox.bottom
    }

    fun moveToPathIndicator(){
        Log.i("moving","true")
        moving = true
        GlobalScope.launch {
            if(currentTile()!=pathIndicatorTile()){
                if(isPathFree(pathIndicator.x, pathIndicator.y)){
                    if(pathFollow) {
                        disablePathFollowing()
                        moveTo(pathIndicator.x, pathIndicator.y)
                    } else if(pathIndicatorTile()!=previousPathIndicatorTile){
                        previousPathIndicatorTile = pathIndicatorTile()
                        moveTo(pathIndicator.x, pathIndicator.y)
                    }
                    delay(66)
                    moveToPathIndicator()
                } else {
                    if(pathIndicatorTile()!=previousPathIndicatorTile){
                        disablePathFollowing()
                        previousPathIndicatorTile=pathIndicatorTile()
                        setupPath(pathIndicator.x, pathIndicator.y)
                        delay(66)
                        moveToPathIndicator()
                    } else if(!pathFollow){
                        pathIndicator.invisible()
                        delay(66)
                        moving = false
                    } else {
                        delay(66)
                        moveToPathIndicator()
                    }
                }
            } else {
                moveTo(pathIndicator.x, pathIndicator.y)
                pathIndicator.invisible()
                delay(66)
                moving = false
            }
        }
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
            disablePathFollowing()
            game.map.currentRoom().close()
            while (!game.map.nextRoom().characterInStartPosition(game)) {
                game.map.nextRoom().placeCharacter(game)
                pathIndicator.x = sprite.x
                pathIndicator.y = sprite.y
            }

            if(game.map.currentRoom() is LongRoom || game.map.currentRoom() is LargeRoom){
                temporaryMovingInteraction = {
                    x, y ->  
                }
            }
            stun()
            game.nextRoom()
        } else {
            temporaryMovingInteraction(x,y)
            sprite.x = x
            sprite.y = y
            val collectibleListCopy = game.collectibleList.toList()
            with(collectibleListCopy.iterator()){
                forEach {
                    if(inBoundingBox(it.sprite.x, it.sprite.y)){
                        it.collectEffect()
                        it.destroy()
                    }
                }
            }
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
            !it.permanent
        }
        hearts = newHearts
        game.ath["hearts"] = newHearts
    }

    fun blink(){
        GlobalScope.launch {
            if(remainingInvulnerability && !game.pause){
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