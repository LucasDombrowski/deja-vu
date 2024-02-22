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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Camera(val game: Game) {
    val sprite = BasicSprite(R.drawable.transparent,game.map.characterStartPosition().first, game.map.characterStartPosition().second)
    val speed = 100f

    fun moveTo(x: Float, y:Float){
        GlobalScope.launch {
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
                moveTo(x,y)
            } else {
                game.resetCollectibles()
                game.controllableCharacter!!.restart()
                delay(1000)
                if(game.map.currentRoom() is BasicRoom){
                    game.map.currentRoom().spawnEnemies()
                }
                if(game.map.currentRoom() is LongRoom){
                    val minXValue = min(
                        (game.map.currentRoom() as LongRoom).getFirstHalfCenter().first,
                        (game.map.currentRoom() as LongRoom).getSecondHalfCenter().first
                    )
                    val maxXValue = max(
                        (game.map.currentRoom() as LongRoom).getFirstHalfCenter().first,
                        (game.map.currentRoom() as LongRoom).getSecondHalfCenter().first
                    )
                    game.controllableCharacter!!.temporaryMovingInteraction = {
                            x, y ->
                        if(x<minXValue){
                            sprite.x = minXValue
                        } else if(x>maxXValue){
                            sprite.x = maxXValue
                        } else {
                            sprite.x = x
                        }
                    }
                }
                if(game.map.currentRoom() is LargeRoom){
                    val minYValue = min(
                        (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
                        (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
                    )
                    val maxYValue = max(
                        (game.map.currentRoom() as LargeRoom).getFirstHalfCenter().second,
                        (game.map.currentRoom() as LargeRoom).getSecondHalfCenter().second
                    )
                    game.controllableCharacter!!.temporaryMovingInteraction = {
                            x, y ->
                        if(y<minYValue){
                            sprite.y = minYValue
                        } else if(y>maxYValue){
                            sprite.y = maxYValue
                        } else {
                            sprite.y = y
                        }
                    }
                }
                if(game.map.currentRoom() is BossRoom){
                    game.spawnBoss()
                }
                if(game.map.currentRoom() is ShopRoom){
                    (game.map.currentRoom() as ShopRoom).launchCinematic(game)
                }
                game.controllableCharacter!!.getClosestEnemy()
            }
        }
    }
}