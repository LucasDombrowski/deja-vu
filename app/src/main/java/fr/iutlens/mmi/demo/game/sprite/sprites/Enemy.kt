package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import java.util.LinkedList
import java.util.Queue
import java.util.Stack
import kotlin.math.round

open class Enemy(
    sprite: BasicSprite,
    game: Game,
    speed:Float,
    hearts: MutableList<Heart>,
    basicAnimationSequence: List<Int>,
    leftAnimationSequence: List<Int> = basicAnimationSequence,
    topAnimationSequence: List<Int> = basicAnimationSequence,
    rightAnimationSequence: List<Int> = basicAnimationSequence,
    bottomAnimationSequence : List<Int> = basicAnimationSequence,
    target : Character? = null,
    fireRate : Long = 0,
    open var action : Job = GlobalScope.launch {
        return@launch
    }
) : Character(
    sprite = sprite,
    game = game,
    speed = speed,
    hearts = hearts,
    basicAnimationSequence = basicAnimationSequence,
    leftAnimationSequence = leftAnimationSequence,
    topAnimationSequence = topAnimationSequence,
    rightAnimationSequence = rightAnimationSequence,
    bottomAnimationSequence = bottomAnimationSequence,
    target = target,
    fireRate = fireRate
){
    open fun hit(damages: Float, knockback: Float, direction: String){
        healthDown(damages, knockback, direction)
        GlobalScope.launch {
            sprite.semiWhiteColor()
            delay(100)
            sprite.permanentColor()
            if(filledHeart()<=hearts.size/2 && filledHeart()>hearts.size/4){
                sprite.midLifeColor()
            } else if(filledHeart()<=hearts.size/4){
                sprite.lowLifeColor()
            }
        }

    }

    open fun spawn(x: Float, y: Float){
        game.addCharacter(this)
        changePos(x, y)
    }

    override fun copy() : Enemy{
        return Enemy(sprite.copy(), game, speed, hearts, basicAnimationSequence, leftAnimationSequence, topAnimationSequence, rightAnimationSequence, bottomAnimationSequence, target, fireRate, action)
    }

    fun getShortestPath(x: Float, y: Float) : ArrayDeque<Pair<Int,Int>>{
        val indexes = game.map.getMapIndexFromPosition(x,y)
        val mapPath = generatePathMap(indexes.first, indexes.second)
        var currentKey = mapPath.filterValues {
            indexes in it
        }.keys.first()
        val stack = ArrayDeque<Pair<Int,Int>>()
        while (currentKey != mapPath.keys.first()){
            stack.push(currentKey)
            currentKey = mapPath.filterValues {
                currentKey in it
            }.keys.first()
        }
        return stack
    }

    fun generatePathMap(x: Int, y: Int) : MutableMap<Pair<Int,Int>,List<Pair<Int,Int>>>{
        val pathMap = mutableMapOf<Pair<Int,Int>,List<Pair<Int,Int>>>()
        val indexSpritePosition = game.map.getMapIndexFromPosition(
            sprite.x,
            sprite.y
        )
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.offer(
            Pair(
            indexSpritePosition.first,
            indexSpritePosition.second
            )
        )
        while (queue.isNotEmpty()){
            val current = queue.poll()
            if (current != null && current !in pathMap.keys) {
                if(current.first == x && current.second == y){
                    break
                } else {
                    generatePathList(pathMap,queue,current.first,current.second)
                }
            }
        }
        return pathMap
    }

    fun generatePathList(pathMap: MutableMap<Pair<Int,Int>,List<Pair<Int,Int>>>, queue: Queue<Pair<Int,Int>>,x:Int, y:Int){
        val availableTiles = mutableListOf<Pair<Int,Int>>()
        if(tileAvailable(x+1,y)){
            availableTiles.add(Pair(x+1,y))
            queue.offer(Pair(x+1,y))
        }
        if(tileAvailable(x-1,y)){
            availableTiles.add(Pair(x-1,y))
            queue.offer(Pair(x-1,y))
        }
        if(tileAvailable(x,y+1)){
            availableTiles.add(Pair(x,y+1))
            queue.offer(Pair(x,y+1))
        }
        if(tileAvailable(x,y-1)){
            availableTiles.add(Pair(x,y-1))
            queue.offer(Pair(x,y-1))
        }
        pathMap[Pair(x,y)] = availableTiles.toList()
    }

    fun tileAvailable(x: Int, y: Int) : Boolean{
        val floatPosition : Pair<Float,Float> = Pair(
            game.map.getPositionFromMapIndex(x,y).first,
            game.map.getPositionFromMapIndex(x,y).second
        )
        return !game.map.inForbiddenArea(floatPosition.first, floatPosition.second)
    }

    fun filledHeart() : Int{
        return hearts.filter {
            it.filled>0f
        }.size
    }

    fun smokeAnimation(){
        val smoke = BasicSprite(R.drawable.smoke_animation,sprite.x,sprite.y)
        game.addSprite(smoke)
        GlobalScope.launch {
            repeat(5){
                delay(100)
                smoke.ndx++
                game.invalidate()
            }
            game.deleteSprite(smoke)
        }
    }

}