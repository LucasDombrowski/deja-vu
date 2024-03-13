package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.rooms.BasicRoom
import fr.iutlens.mmi.demo.game.map.rooms.BossRoom
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.map.rooms.ShopRoom
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.utils.getDistance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Camera(val game: Game) {
    val sprite = BasicSprite(R.drawable.transparent,game.map.characterStartPosition().first, game.map.characterStartPosition().second)
    var cameraMoving : Job = GlobalScope.launch {

    }
    fun moveTo(x: Float, y:Float){
        moveCamera(x,y,{
            action()
        })
    }

    fun moveCamera(x: Float, y: Float, after : ()->Unit = {}, speed: Float = 100f){
        cameraMoving.cancel()
        cameraMoving = GlobalScope.launch {
            if(x!=sprite.x || y!=sprite.y){
                val xChangeValue = when{
                    abs(x-sprite.x)>speed->speed
                    else->(abs(x-sprite.x)%speed)
                }
                val yChangeValue = when{
                    abs(y-sprite.y)>speed->speed
                    else->(abs(y-sprite.y)%speed)
                }
                when{
                    x<sprite.x->sprite.x-=xChangeValue
                    x>sprite.x->sprite.x+=xChangeValue
                    else->sprite.x
                }
                when{
                    y<sprite.y->sprite.y-=yChangeValue
                    y>sprite.y->sprite.y+=yChangeValue
                    else->sprite.y
                }
                game.invalidate()
                delay(33)
                moveCamera(x,y,after)
            } else {
                after()
            }
        }
    }

    fun slideLongRoomCamera() : (x: Float, y: Float)->Unit{
        val minXValue = min(
            (game.map.currentRoom() as LongRoom).getFirstHalfCenter().first,
            (game.map.currentRoom() as LongRoom).getSecondHalfCenter().first
        )
        val maxXValue = max(
            (game.map.currentRoom() as LongRoom).getFirstHalfCenter().first,
            (game.map.currentRoom() as LongRoom).getSecondHalfCenter().first
        )
        val maxCameraDistance = 4*game.map.tileArea.w
        return {
            x, y ->
            if(distanceXWithCamera()>maxCameraDistance){
                when{
                    x<sprite.x->{
                        if(sprite.x-(distanceXWithCamera()-maxCameraDistance)>minXValue){
                            sprite.x-=(distanceXWithCamera()-maxCameraDistance)
                        } else {
                            sprite.x = minXValue
                        }
                    }
                    else->{
                        if(sprite.x+(distanceXWithCamera()-maxCameraDistance)<maxXValue){
                            sprite.x+=(distanceXWithCamera()-maxCameraDistance)
                        } else {
                            sprite.x = maxXValue
                        }
                    }
                }
            }
        }
    }

    fun distanceXWithCamera() : Float{
        return abs(game.controllableCharacter!!.sprite.x - sprite.x)
    }

    fun distanceYWithCamera() : Float{
        return abs(game.controllableCharacter!!.sprite.y - sprite.y)
    }

    fun slideLargeRoomCamera() : (x: Float, y:Float)->Unit{
        val minYValue = min(
            (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
            (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
        )
        val maxYValue = max(
            (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
            (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
        )
        val maxCameraDistance = 2*game.map.tileArea.h
        return {
            x, y ->
            if(distanceYWithCamera()>maxCameraDistance){
                when{
                    y<sprite.y->{
                        if(sprite.y-(distanceYWithCamera()-maxCameraDistance)>minYValue){
                            sprite.y-=(distanceYWithCamera()-maxCameraDistance)
                        } else {
                            sprite.y = minYValue
                        }
                    }
                    else->{
                        if(sprite.y+(distanceYWithCamera()-maxCameraDistance)<maxYValue){
                            sprite.y+=(distanceYWithCamera()-maxCameraDistance)
                        } else {
                            sprite.y = maxYValue
                        }
                    }
                }
            }

        }
    }

    fun action(){
        GlobalScope.launch {
            game.resetCollectibles()
            game.controllableCharacter!!.restart()
            delay(1000)
            if (game.map.currentRoom() is BasicRoom || game.map.currentRoom() is LongRoom || game.map.currentRoom() is LargeRoom) {
                game.map.currentRoom().spawnEnemies()
                game.map.currentRoom().startChallenge(game)
            }
            if (game.map.currentRoom() is LongRoom) {
                game.controllableCharacter!!.temporaryMovingInteraction = slideLongRoomCamera()
            }
            if (game.map.currentRoom() is LargeRoom) {
                game.controllableCharacter!!.temporaryMovingInteraction = slideLargeRoomCamera()
            }
            if (game.map.currentRoom() is BossRoom) {
                game.spawnBoss()
            }
            if (game.map.currentRoom() is ShopRoom) {
                (game.map.currentRoom() as ShopRoom).launchCinematic(game)
            }
            game.controllableCharacter!!.getClosestEnemy()
        }
    }
}