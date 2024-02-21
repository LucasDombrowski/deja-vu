package fr.iutlens.mmi.demo.game.sprite.sprites

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.Coin
import fr.iutlens.mmi.demo.game.gameplayResources.collectibles.HeartContainer
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round
import kotlin.reflect.typeOf


open class Character(
    var sprite: BasicSprite,
    val game: Game,
    var speed:Float,
    var hearts: MutableList<Heart>,
    var fireRate : Long = 0,
    val invulnerability: Long = 0,
    val basicAnimationSequence: List<Int>,
    val leftAnimationSequence: List<Int> = basicAnimationSequence,
    val topAnimationSequence: List<Int> = basicAnimationSequence,
    val rightAnimationSequence: List<Int> = basicAnimationSequence,
    val bottomAnimationSequence : List<Int> = basicAnimationSequence,
    val animationDelay : Long = 100,
    var target : Character? = null,
    var reflect : Boolean = false){


    var movingAction : Job = GlobalScope.launch {
        return@launch
    }
    var alive = true
    var mobile = true
    var remainingInvulnerability : Boolean = false
    var animationTick = 0
    var previousDirection = "static"
    var currentDirection = "static"
    var currentAnimationSequence = basicAnimationSequence
    var currentAnimationSequenceIndex : Int = 0
    val characterAnimation : Job = setInterval(0, animationDelay){
        GlobalScope.launch {
            if(currentAnimationSequenceIndex>=currentAnimationSequence.size-1 || game.pause){
                currentAnimationSequenceIndex = 0
                sprite.ndx = currentAnimationSequence[0]
            } else {
                currentAnimationSequenceIndex ++
                sprite.ndx = currentAnimationSequence[currentAnimationSequenceIndex]
            }

        }

    }
    open fun changePos(x: Float, y: Float){
        if(game.map.inForbiddenArea(
            x,
            y
        )){
            GlobalScope.launch {
                stun()
                delay(33)
                restart()
            }
        } else {
            sprite.x = x
            sprite.y = y
        }
        game.invalidate()
    }


    fun realSpeed() : Float{
        return speed*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }

    fun realKnockback(knockback: Float) : Float{
        return knockback*((game.map.tileArea.w + game.map.tileArea.h)/2)
    }
    fun changeFrame(n: Int){
        sprite.ndx = n
        game.invalidate()
    }

    fun moveTo(x: Float, y:Float){
        if(mobile){
            if((this==game.controllableCharacter!! && !game.movingRestriction) || this!=game.controllableCharacter!!) {
                previousDirection = currentDirection
                movingAction.cancel()
                movingAction = GlobalScope.launch {
                    while (game.pause){
                        delay(33)
                    }
                    if (round(x) != round(sprite.x) || round(y) != round(sprite.y)) {
                        val xDifference = when {
                            abs(x - sprite.x) >= realSpeed() -> realSpeed()
                            else -> abs(x - sprite.x) % realSpeed()
                        }
                        val yDifference = when {
                            abs(y - sprite.y) >= realSpeed() -> realSpeed()
                            else -> abs(y - sprite.y) % realSpeed()
                        }
                        val nextX = when {
                            x < sprite.x -> sprite.x - xDifference
                            x > sprite.x -> sprite.x + xDifference
                            else -> sprite.x
                        }
                        val nextY = when {
                            y < sprite.y -> sprite.y - yDifference
                            y > sprite.y -> sprite.y + yDifference
                            else -> sprite.y
                        }
                        currentDirection = when {
                            nextX < sprite.x && nextY == sprite.y -> "left"
                            nextX > sprite.x && nextY == sprite.y -> "right"
                            nextX < sprite.x && nextY < sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "top"
                            } else {
                                "left"
                            }

                            nextX < sprite.x && nextY > sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "bottom"
                            } else {
                                "left"
                            }

                            nextX > sprite.x && nextY < sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "top"
                            } else {
                                "right"
                            }

                            nextX > sprite.x && nextY > sprite.y -> if (previousDirection == "top" || previousDirection == "bottom") {
                                "bottom"
                            } else {
                                "right"
                            }

                            nextX == sprite.x && nextY < sprite.y -> "top"
                            else -> "bottom"
                        }
                        if(previousDirection!=currentDirection){
                            currentAnimationSequenceIndex = 0
                        }
                        currentAnimationSequence = when(currentDirection){
                            "left"->leftAnimationSequence
                            "right"->rightAnimationSequence
                            "top"->topAnimationSequence
                            "bottom"->bottomAnimationSequence
                            else->basicAnimationSequence
                        }

                        changePos(nextX, nextY)
                        delay(33)
                        moveTo(x, y)
                    } else {
                        stun()
                        restart()
                    }
                }
            }
        }

    }

    fun stun(){
        movingAction.cancel()
        mobile = false
        currentDirection = "static"
        currentAnimationSequence = basicAnimationSequence
    }

    fun restart(){
        mobile = true
    }
    fun inBoundingBox(x: Float, y:Float) : Boolean{
        return (sprite.boundingBox.left < x &&
                sprite.boundingBox.right > x &&
                sprite.boundingBox.top < y &&
                sprite.boundingBox.bottom > y)
    }


    fun healthDown(n: Float, knockback: Float = 0f, direction: String = "static"){
        if(!remainingInvulnerability) {
            if(invulnerability>0){
                remainingInvulnerability = true
                decreaseInvulnerability()
            }
            var healthToRemove = n
            var heartIndex = hearts.lastIndex
            while(healthToRemove>0 && heartIndex>=0){
                while (hearts[heartIndex].filled>0f && healthToRemove>0){
                    hearts[heartIndex].filled-=0.25f
                    healthToRemove-=0.25f
                }
                if(!hearts[heartIndex].permanent && hearts[heartIndex].filled<=0){
                    hearts.removeAt(heartIndex)
                }
                heartIndex--
            }
            getKnockback(knockback,direction)
            if(this is MainCharacter){
                refreshHeathBar()
                blink()
            } else if(hearts[0].filled<=0){
                die()
            }
        }
    }

    open fun getKnockback(knockback: Float, direction: String){
        movingAction.cancel()
        val xMultiplier= when(direction){
            "left"->-1
            "right"->1
            else->0
        }
        val yMultiplier = when(direction){
            "top"->-1
            "bottom"->1
            "left"->0
            "right"->0
            else->1
        }
        GlobalScope.launch {
            repeat(10){
                changePos(sprite.x+(realKnockback(knockback)/10*xMultiplier), sprite.y + (realKnockback(knockback)/10*yMultiplier))
                delay(33)
            }
        }
    }

    open fun copy() : Character{
        return Character(
            sprite.copy(), game, speed, hearts, fireRate, invulnerability, basicAnimationSequence, leftAnimationSequence, topAnimationSequence, bottomAnimationSequence, target = target
        )
    }

    fun getReverseDirection() : String{
        return when(currentDirection){
            "left"->"right"
            "right"->"left"
            "top"->"bottom"
            "bottom"->"top"
            else->"static"
        }
    }
    fun decreaseInvulnerability(){
        GlobalScope.launch {
            delay(invulnerability)
            remainingInvulnerability = false
        }
    }

    fun die(){
        alive = false
        game.deleteSprite(sprite)
        game.deleteCharacter(character = this)
        movingAction.cancel()
        if(this is Enemy && this !is Boss){
            smokeAnimation()
            action.cancel()
            game.map.currentRoom().isOpenable(game)
            Coin(game).drop(sprite.x, sprite.y)
        }
        if(this is Enemy && this is Boss){
            action.cancel()
            HeartContainer(game).setup(sprite.x, sprite.y)
        }
        if(this==game.controllableCharacter!!.target){
            game.controllableCharacter!!.getClosestEnemy()
        }
        game.invalidate()
    }

    var currentPath : List<Pair<Int,Int>> = listOf()

    var followingPath : Job = GlobalScope.launch {
        return@launch
    }
    var pathFollow : Boolean = false

    fun isPathFree(x: Float, y: Float) : Boolean{
        val startTile = game.map.getMapIndexFromPosition(sprite.x, sprite.y)
        val aimedTile = game.map.getMapIndexFromPosition(x,y)
        var startCol = when{
            sprite.x<x->startTile.second
            else->aimedTile.second
        }
        var startRow = when{
            sprite.y<y->startTile.first
            else->aimedTile.first
        }
        var endCol = when{
            sprite.x<x->aimedTile.second
            else->startTile.second
        }
        var endRow = when{
            sprite.y<y->aimedTile.first
            else->startTile.first
        }
        val startPosition = game.map.currentRoom().getPosition(startRow, startCol)
        val endPosition = game.map.currentRoom().getPosition(endRow, endCol)
        val roomList = game.map.currentRoom().toList()
        for(i in startPosition.first..endPosition.first){
            for(j in startPosition.second..endPosition.second){
                if(roomList[i][j] !in game.map.authorizedTiles){
                    return false
                }
            }
        }
        return true
    }

    fun setupPath(x: Float,y: Float){
        disablePathFollowing()
        if(!game.pause && game.map.currentRoom().inMinMaxCoordinates(x,y)) {
            if (isPathFree(x, y)) {
                moveTo(x, y)
            } else {
                findShortestPath(x, y)
            }
        }
    }
    fun findShortestPath(x: Float, y: Float){
        GlobalScope.launch {
            val currentTile = game.map.getMapIndexFromPosition(sprite.x, sprite.y)
            var aimedTile = game.map.getMapIndexFromPosition(x,y)
            if(!isAvailableTile(row = aimedTile.first, col = aimedTile.second)){
                aimedTile = closestAvailableTile(aimedTile.first, aimedTile.second)
            }
            currentPath = getShortestPath(currentTile, aimedTile)
            enablePathFollowing()
        }
    }

    fun closestAvailableTile(row: Int, col: Int, factor: Int = 1) : Pair<Int,Int>{
        return if(isAvailableTile(row = row+factor, col = col)){
            Pair(row+factor,col)
        } else if(isAvailableTile(row = row-factor, col = col)){
            Pair(row-factor,col)
        } else if(isAvailableTile(row = row, col = col+factor)){
            Pair(row,col+factor)
        } else if(isAvailableTile(row = row, col = col-factor)){
            Pair(row,col-factor)
        } else {
            closestAvailableTile(row,col,factor+1)
        }

    }

    fun setupPathFollowing(){
        var currentPathIndex = 0
        followingPath = setInterval(0,33){
            val pathToFollow = currentPath.toList()
            if(game.map.getMapIndexFromPosition(sprite.x, sprite.y) == pathToFollow[currentPathIndex]){
                currentPathIndex++
            }
            if(currentPathIndex >= pathToFollow.size){
                disablePathFollowing()
            } else {
                val floatValue = game.map.getPositionFromMapIndex(pathToFollow[currentPathIndex].first, pathToFollow[currentPathIndex].second)
                moveTo(floatValue.first + game.map.tileArea.w/2, floatValue.second + game.map.tileArea.h/2)
            }
        }
    }

    fun disablePathFollowing(){
        pathFollow = false
        followingPath.cancel()
    }

    fun enablePathFollowing(){
        pathFollow = true
        setupPathFollowing()
    }

    fun getShortestPath(start: Pair<Int,Int>, end: Pair<Int,Int>) : List<Pair<Int,Int>>{
        val ways = mutableMapOf<Int,List<List<Pair<Int,Int>>>>(0 to listOf(listOf(start)))
        val visited = mutableListOf<Pair<Int,Int>>()
        var currentTilesNumber = 0

        fun endTileFound() : Boolean{
            return ways.filterValues {
                it.filter {
                    it.contains(end)
                }.isNotEmpty()
            }.isNotEmpty()
        }


        while (!endTileFound()){
            val list = ways[currentTilesNumber]
            val newList = mutableListOf<List<Pair<Int,Int>>>()
            with(list!!.iterator()){
                forEach {
                    val last = it.last()
                    if(isAvailableTile(visited,last.first + 1,last.second)){
                        newList.add(it.toList() + Pair(last.first+1, last.second))
                    }
                    if(isAvailableTile(visited,last.first - 1,last.second)){
                        newList.add(it.toList() + Pair(last.first-1, last.second))
                    }
                    if(isAvailableTile(visited,last.first,last.second+1)){
                        newList.add(it.toList() + Pair(last.first, last.second+1))
                    }
                    if(isAvailableTile(visited,last.first,last.second-1)){
                        newList.add(it.toList() + Pair(last.first, last.second-1))
                    }
                    visited.add(last)
                }
            }
            currentTilesNumber++
            ways[currentTilesNumber] = newList.toList()
        }

        return ways.filterValues {
            it.filter {
                it.contains(end)
            }.isNotEmpty()
        }.values.first().filter {
            it.contains(end)
        }.first()

    }



    fun isAvailableTile(visited : MutableList<Pair<Int,Int>> = mutableListOf(), row: Int, col: Int) : Boolean{
        val floatCoordinates = game.map.getPositionFromMapIndex(row,col)
        return inCurrentRoom(row,col) && !game.map.inForbiddenArea(floatCoordinates.first, floatCoordinates.second) && Pair(row, col) !in visited
    }
    fun inCurrentRoom(row: Int, column : Int) : Boolean{
        val minMaxIndices = game.map.currentRoom().getMinMaxIndices()
        return row in minMaxIndices.first.first..minMaxIndices.second.first && column in minMaxIndices.first.second..minMaxIndices.second.second
    }

    fun getTileDifferences(x1: Int, y1: Int, x2: Int, y2: Int) : Pair<Int,Int>{
        return Pair(
            abs(x1-x2),
            abs(y1-y2)
        )
    }

}




