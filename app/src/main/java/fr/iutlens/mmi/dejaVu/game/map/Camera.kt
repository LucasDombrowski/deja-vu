package fr.iutlens.mmi.dejaVu.game.map

import android.util.Log
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.map.rooms.BasicRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.BossRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.LargeRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.LongRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.ShopRoom
import fr.iutlens.mmi.dejaVu.game.map.rooms.TreasureRoom
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialChest
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialFighting
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.utils.getDistance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Camera(val game: Game) {
    val sprite = BasicSprite(R.drawable.transparent,game.map.characterStartPosition().first, game.map.characterStartPosition().second)
    var fightingTutorial = false
    var chestTutorial = false
    var cameraMoving : Job = GlobalScope.launch {

    }
    fun moveTo(x: Float, y:Float){
        moveCamera(x,y,{
            action()
        })
    }

    fun moveCamera(x: Float, y: Float, after : ()->Unit = {}, floatSpeed: Float = 0.6f){
        val speed = realSpeed(floatSpeed)
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
                moveCamera(x,y,after,floatSpeed)
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
        val exitSide = (game.map.currentRoom() as LongRoom).exitSide
        val maxCameraDistance = 4*game.map.tileArea.w
        val roomCenter = game.map.currentRoom().getRoomCenter()
        val endLocalTile = game.map.currentRoom().findEndPosition(game.map.currentRoom().toList().map{
            it.map {
                it.single()
            }
        })
        val endGlobalTile = game.map.currentRoom().getGlobalPosition(endLocalTile!!.first, endLocalTile.second)
        return {
            x, y ->
            if(game.controllableCharacter!!.sprite.x < minXValue){
                moveCamera(minXValue,sprite.y, floatSpeed = 0.2f)
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)) {
                    if(exitSide=="left"){
                        arrowToRoomExit()
                    } else {
                        setDirectionArrow(0f)
                    }
                } else {
                    removeDirectionArrow()
                }
            } else if(game.controllableCharacter!!.sprite.x > maxXValue){
                moveCamera(maxXValue,sprite.y, floatSpeed = 0.2f)
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)){
                    if(exitSide=="right"){
                        arrowToRoomExit()
                    } else {
                        setDirectionArrow(180f)
                    }
                } else {
                    removeDirectionArrow()
                }
            } else{
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)){
                    if(exitSide=="left"){
                        setDirectionArrow(180f)
                    } else {
                        setDirectionArrow(0f)
                    }
                } else {
                    removeDirectionArrow()
                }
                if(distanceXWithCamera()>maxCameraDistance) {
                    when {
                        x < sprite.x -> {
                            if (sprite.x - (distanceXWithCamera() - maxCameraDistance) > minXValue) {
                                sprite.x -= (distanceXWithCamera() - maxCameraDistance)
                            } else {
                                sprite.x = minXValue
                            }
                        }

                        else -> {
                            if (sprite.x + (distanceXWithCamera() - maxCameraDistance) < maxXValue) {
                                sprite.x += (distanceXWithCamera() - maxCameraDistance)
                            } else {
                                sprite.x = maxXValue
                            }
                        }
                    }
                }
            }
        }
    }

    fun characterCloseToDoor(endTile : Pair<Int,Int>): Boolean {
        val characterTile = game.map.getMapIndexFromPosition(game.controllableCharacter!!.sprite.x, game.controllableCharacter!!.sprite.y)
        return when(game.map.currentRoom().exit){
            "top","bottom"->abs(characterTile.first - endTile.first)<=1
            "right","left"->abs(characterTile.second - endTile.second)<=1
            else->false
        }
    }

    fun arrowToRoomExit(){
        when(game.map.currentRoom().exit){
            "top"->setDirectionArrow(270f)
            "right"->setDirectionArrow(0f)
            "bottom"->setDirectionArrow(90f)
            else->setDirectionArrow(180f)
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
        val exitSide = (game.map.currentRoom() as LargeRoom).exitSide
        val roomCenter = game.map.currentRoom().getRoomCenter()
        val endLocalTile = game.map.currentRoom().findEndPosition(game.map.currentRoom().toList().map{
            it.map {
                it.single()
            }
        })
        val endGlobalTile = game.map.currentRoom().getGlobalPosition(endLocalTile!!.first, endLocalTile.second)
        return {
            x, y ->
            if(game.controllableCharacter!!.sprite.y < minYValue){
                moveCamera(sprite.x,minYValue, floatSpeed = 0.2f)
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)){
                    if(exitSide=="top"){
                        arrowToRoomExit()
                    } else {
                        setDirectionArrow(90f)
                    }
                } else {
                    removeDirectionArrow()
                }
            } else if(game.controllableCharacter!!.sprite.y > maxYValue){
                moveCamera(sprite.x,maxYValue, floatSpeed = 0.2f)
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)){
                    if(exitSide=="bottom"){
                        arrowToRoomExit()
                    } else {
                        setDirectionArrow(270f)
                    }
                } else {
                    removeDirectionArrow()
                }
            } else {
                if(game.map.currentRoom().open && !characterCloseToDoor(endGlobalTile)){
                    if(exitSide=="bottom"){
                        setDirectionArrow(90f)
                    } else {
                        setDirectionArrow(270f)
                    }
                } else {
                    removeDirectionArrow()
                }
                if(distanceYWithCamera()>maxCameraDistance) {
                    when {
                        y < sprite.y -> {
                            if (sprite.y - (distanceYWithCamera() - maxCameraDistance) > minYValue) {
                                sprite.y -= (distanceYWithCamera() - maxCameraDistance)
                            } else {
                                sprite.y = minYValue
                            }
                        }

                        else -> {
                            if (sprite.y + (distanceYWithCamera() - maxCameraDistance) < maxYValue) {
                                sprite.y += (distanceYWithCamera() - maxCameraDistance)
                            } else {
                                sprite.y = maxYValue
                            }
                        }
                    }
                }
            }
        }
    }

    fun realSpeed(speed: Float): Float {
        return speed * ((game.map.tileArea.w + game.map.tileArea.h) / 2)
    }

    fun action(){
        GlobalScope.launch {
            game.resetCollectibles()
            game.controllableCharacter!!.restart()
            if(game.map.currentRoom() is TreasureRoom && game.firstTime && !chestTutorial && !checkedTutorials["chest"]!!){
                game.cinematic.value = Pair(
                    TutorialChest(game){
                        chestTutorial = true
                        commitBooleanSharedPreferences("chestTutorial",true)
                    },
                    true
                )
            }
            delay(1000)
            if ((game.map.currentRoom() is BasicRoom || game.map.currentRoom() is LongRoom || game.map.currentRoom() is LargeRoom) && game.map.currentRoom() !is BossRoom) {
                game.map.currentRoom().spawnEnemies()
                game.map.currentRoom().startChallenge(game)
                game.addSprite(game.controllableCharacter!!.targetIndicator)
                if(game.firstTime && !fightingTutorial && !checkedTutorials["fighting"]!!){
                    game.cinematic.value = Pair(
                        TutorialFighting(game){
                            fightingTutorial = true
                            commitBooleanSharedPreferences("fightingTutorial",true)
                        }, true
                    )
                }
            }
            if (game.map.currentRoom() is LongRoom) {
                game.controllableCharacter!!.temporaryMovingInteraction = slideLongRoomCamera()
            }
            if (game.map.currentRoom() is LargeRoom) {
                game.controllableCharacter!!.temporaryMovingInteraction = slideLargeRoomCamera()
            }
            if (game.map.currentRoom() is BossRoom) {
                game.spawnBoss()
                game.addSprite(game.controllableCharacter!!.targetIndicator)
            }
            if (game.map.currentRoom() is ShopRoom) {
                (game.map.currentRoom() as ShopRoom).launchCinematic(game)
            }

            game.ath["clearedRooms"] = game.map.rooms!!.subList(0,game.map.currentRoom+1)
        }
    }

    fun setDirectionArrow(rotate: Float){
        game.continueArrow.value = Pair(true,rotate)
    }

    fun removeDirectionArrow(){
        if(game.continueArrow.value.first) {
            game.continueArrow.value = Pair(false, game.continueArrow.value.second)
        }
    }
}