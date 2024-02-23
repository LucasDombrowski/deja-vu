package fr.iutlens.mmi.demo.game.map

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.rooms.BasicRoom
import fr.iutlens.mmi.demo.game.map.rooms.BossRoom
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.map.rooms.ShopRoom
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
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
        val midXValue = game.map.currentRoom().getRoomCenter().first
        val maxXValue = max(
            (game.map.currentRoom() as LongRoom).getFirstHalfCenter().first,
            (game.map.currentRoom() as LongRoom).getSecondHalfCenter().first
        )
        val firstCamTrigger = minXValue + (midXValue - minXValue) / 2
        val secondCamTrigger = midXValue + (maxXValue - midXValue) / 2
        return{ x, y ->
            if (x < firstCamTrigger) {
                cameraMoving.cancel()
                game.controllableCharacter!!.temporaryMovingInteraction = {
                        x,y->
                }
                moveCamera(minXValue, sprite.y, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLongRoomCamera()
                })
            } else if(x>secondCamTrigger){
                cameraMoving.cancel()
                game.controllableCharacter!!.temporaryMovingInteraction = {
                        x,y->
                }
                moveCamera(maxXValue, sprite.y, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLongRoomCamera()
                })
            } else {
                cameraMoving.cancel()
                game.controllableCharacter!!.temporaryMovingInteraction = {
                        x,y->
                }
                moveCamera(midXValue,sprite.y, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLongRoomCamera()
                })
            }
        }
    }

    fun slideLargeRoomCamera() : (x: Float, y:Float)->Unit{
        val minYValue = min(
            (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
            (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
        )
        val midYValue = game.map.currentRoom().getRoomCenter().second
        val maxYValue = max(
            (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
            (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
        )
        val firstCamTrigger = minYValue + (midYValue - minYValue) / 2
        val secondCamTrigger = midYValue + (maxYValue - midYValue) / 2
        return { x, y ->
            if(y<firstCamTrigger){
                cameraMoving.cancel()
                game.controllableCharacter!!.temporaryMovingInteraction = {
                        x,y->
                }
                moveCamera(sprite.x, minYValue, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLargeRoomCamera()
                })
            } else if(y>secondCamTrigger){
                cameraMoving.cancel()
                game.controllableCharacter!!.temporaryMovingInteraction = {
                        x,y->
                }
                moveCamera(sprite.x, maxYValue, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLargeRoomCamera()
                })
            } else {
                cameraMoving.cancel()
                moveCamera(sprite.x, midYValue, speed = 1f, after = {
                    game.controllableCharacter!!.temporaryMovingInteraction = slideLargeRoomCamera()
                })
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