package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Challenge
import fr.iutlens.mmi.demo.game.gameplayResources.challenges.SpeedUp
import fr.iutlens.mmi.demo.game.map.rooms.BasicRoom
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs
import kotlin.reflect.KClass
import kotlin.math.log

open class Room(val row: Int, val col: Int, val map: Map, var enter: String ?= null, var exit : String ?= null, var open : Boolean = false, val enemies : IntRange, val challenge: Challenge ? = null) {

    open var composition : String = create().trimIndent()
    var topLeftCorner : Pair<Int,Int> ?= null
    var bottomRightCorner : Pair<Int,Int> ?= null
    var enemyList : MutableList<Enemy> = mutableListOf()
    var roomList : MutableList<MutableList<String>> ? = null

    open fun copy() : Room{
        return Room(row, col, map, enter, exit, open, enemies, challenge)
    }

    fun randomTile(obstacles : Boolean = true): Char {
        when(obstacles){
            true->when((1..10).random()){
                1->return 'J'
                else->{
                    when((1..3).random()){
                        1->return 'G'
                        2->return 'H'
                        else->return 'I'
                    }
                }
            }
            else->{
                when((1..3).random()){
                    1->return 'G'
                    2->return 'H'
                    else->return 'I'
                }
            }
        }
    }
    open fun create() : String {
        val theMap = StringBuilder()
        for(i in 0..<row){
            when(i){
                0->{
                    for(j in 0..<col){
                        when(j){
                            0->theMap.append("0")
                            col-1->theMap.append("1")
                            (col-1)/2->{
                                if(enter=="top" || exit=="top"){
                                    theMap.append("8")
                                } else {
                                    theMap.append("2")
                                }
                            }
                            else->theMap.append("2")
                        }
                    }
                }
                row-1->{
                    for(j in 0..<col){
                        when(j){
                            0->theMap.append("4")
                            (col-1)/2->{
                                if(enter=="bottom" || exit=="bottom"){
                                    theMap.append("9")
                                } else {
                                    theMap.append("7")
                                }
                            }
                            col-1->theMap.append("5")
                            else->theMap.append("7")
                        }
                    }
                }
                else->{
                    for(j in 0..<col){
                        when(j){
                            0->when(i){
                                (row-1)/2->{
                                    if(enter=="left" || exit == "left"){
                                        theMap.append("A")
                                    } else {
                                        theMap.append("6")
                                    }
                                }
                                else->theMap.append("6")
                            }
                            col-1->when(i){
                                (row-1)/2->{
                                    if(enter=="right" || exit=="right"){
                                        theMap.append("B")
                                    } else {
                                        theMap.append("3")
                                    }
                                }
                                else->theMap.append("3")
                            }
                            else->theMap.append(randomTile())
                        }
                    }
                }
            }
            theMap.appendLine()
        }

        val mapList = theMap.lines().map {
            it.split("")
        }

        val mapChars = mutableListOf<List<Char>>()

        with(mapList.iterator()){
            forEach {
                val newRow = mutableListOf<Char>();
                with(it.iterator()){
                    forEach {
                        if(it!=""){
                            newRow.add(it.single())
                        }
                    }
                }
                if(!newRow.isEmpty()){
                    mapChars.add(newRow)
                }
            }
        }


        if(enter!=null || exit!=null) {
            val result = isPathAvailable(mapChars)
            if (!result) {
                return create()
            }
        }
        return theMap.toString()
    }


    fun isPathAvailable(map: List<List<Char>>): Boolean {
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        val visited = mutableSetOf<Pair<Int, Int>>()

        val start = findStartPosition(map)
        val end = when(exit){
            null->start
            else->findEndPosition(map)
        }
        if (start == null || end == null) {
            return false
        }

        tryMove(queue,visited,map,start.first,start.second)

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val row = current.first
            val col = current.second

            if (row == end.first && col == end.second) {
                return true
            }

            tryMove(queue, visited, map, row - 1, col)
            tryMove(queue, visited, map, row + 1, col)
            tryMove(queue, visited, map, row, col - 1)
            tryMove(queue, visited, map, row, col + 1)
        }
        return false
    }

    fun tryMove(queue: Queue<Pair<Int, Int>>, visited: MutableSet<Pair<Int, Int>>, map: List<List<Char>>, row: Int, col: Int) {
        val isValidMove = row in 0 until map.size && col in 0 until map[0].size &&
                map[row][col].toString() in this.map.authorizedTiles

        if (isValidMove && !visited.contains(Pair(row, col))) {
            queue.offer(Pair(row, col))
            visited.add(Pair(row, col))
        }
    }

    open fun findStartPosition(map: List<List<Char>>): Pair<Int, Int>? {

        val doorStart = when (enter) {
            "top" -> {
                '8'
            }
            "left" -> {
                'A'
            }
            "right" -> {
                'B'
            }
            else -> {
                '9'
            }
        }

        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == doorStart) {
                    return when(enter){
                        "left"->Pair(i,j+1)
                        "right"->Pair(i,j-1)
                        "top"->Pair(i+1,j)
                        else->Pair(i-1,j)
                    }
                }
            }
        }
        return null
    }

    open fun findEndPosition(map: List<List<Char>>): Pair<Int, Int>? {
        val doorEnd =  when(open){
            true -> when (exit) {
                "top" -> {
                    'C'
                }
                "left" -> {
                    'E'
                }
                "right" -> {
                    'F'
                }
                else -> {
                    'D'
                }
            }
            else->when (exit){
                "top"->'8'
                "left"->'A'
                "right"->'B'
                else->'9'
            }
        }
        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == doorEnd) {
                    return when(exit){
                        "left"->Pair(i,j+1)
                        "right"->Pair(i,j-1)
                        "top"->Pair(i+1,j)
                        else->Pair(i-1,j)
                    }
                }
            }
        }
        return null
    }

    open fun toList() : MutableList<MutableList<String>>{
        val list = composition.split("\n").map {
            it.split("").toMutableList()
        }.toMutableList()
        with(list.iterator()){
            forEach {
                it.removeAll(listOf(""))
            }
        }
        roomList = list
        return list
    }

    fun getRoomCenter() : Pair<Float, Float>{
        val topLeftCornerFloatPos = map.getPositionFromMapIndex(topLeftCorner!!.first, topLeftCorner!!.second)
        val bottomRightCornerFloatPos = map.getPositionFromMapIndex(bottomRightCorner!!.first, bottomRightCorner!!.second)
        val center = getCenter(topLeftCornerFloatPos.first, topLeftCornerFloatPos.second, bottomRightCornerFloatPos.first, bottomRightCornerFloatPos.second)
        return Pair(
            center[0],
            center[1]
        )
    }

    open fun refresh(){
        composition = create().trimIndent()
        toList()
    }

    fun getPosition(row: Int, column:Int) : Pair<Int,Int>{
        val firstValue = when{
            row-topLeftCorner!!.first<0->0
            row-topLeftCorner!!.first>this.row-1->this.row-1
            else->row-topLeftCorner!!.first
        }
        val secondValue = when{
            column-topLeftCorner!!.second<0->0
            column-topLeftCorner!!.second>this.col-1->this.col-1
            else->column-topLeftCorner!!.second
        }
        return Pair(
            firstValue,
            secondValue
        )
    }

    fun getGlobalPosition(row: Int, col: Int) : Pair<Int,Int>{
        return Pair(
            row + topLeftCorner!!.first,
            col + topLeftCorner!!.second
        )
    }
    fun getElement(x: Float, y: Float) : String{
        val globalPosition = map.getMapIndexFromPosition(x,y)
        val localPosition = getPosition(globalPosition.first, globalPosition.second)
        return roomList!![localPosition.first][localPosition.second]
    }

    open fun placeCharacter(game: Game){
        val roomList = roomList

        val startPosition = findStartPosition(roomList!!.map {
            it.map {
                it.single()
            }.toList()
        }.toList())
        val globalPosition = getGlobalPosition(startPosition!!.first, startPosition!!.second)
        val globalFloatPosition = map.getPositionFromMapIndex(globalPosition.first,globalPosition.second)
        game.controllableCharacter!!.changePos(
            globalFloatPosition.first + map.tileArea.w/2,
            globalFloatPosition.second + map.tileArea.h/2
        )
    }

    fun characterInStartPosition(game: Game) : Boolean{
        val roomList = roomList
        val startPosition = findStartPosition(roomList!!.map {
            it.map {
                it.single()
            }.toList()
        }.toList())
        val globalPosition = getGlobalPosition(startPosition!!.first, startPosition!!.second)
        val characterPosition = map.getMapIndexFromPosition(game.controllableCharacter!!.sprite.x, game.controllableCharacter!!.sprite.y)
        return characterPosition.first == globalPosition.first && characterPosition.second == globalPosition.second
    }

    fun getMinMaxCoordinates() : Pair<Pair<Float,Float>,Pair<Float,Float>>{
        return Pair(
            map.getPositionFromMapIndex(topLeftCorner!!.first, topLeftCorner!!.second),
            map.getPositionFromMapIndex(bottomRightCorner!!.first, bottomRightCorner!!.second)
        )
    }

    fun inMinMaxCoordinates(x: Float, y:Float) : Boolean{
        val minMaxCoordinates = getMinMaxCoordinates()
        return x in minMaxCoordinates.first.first..minMaxCoordinates.second.first && y in minMaxCoordinates.first.second..minMaxCoordinates.second.second
    }

    fun getMinMaxIndices() : Pair<Pair<Int,Int>,Pair<Int,Int>>{
        return Pair(
            Pair(
                topLeftCorner!!.first+1, topLeftCorner!!.second+1
            ),
            Pair(
                bottomRightCorner!!.first-1, bottomRightCorner!!.second-1
            )
        )
    }

    fun spawnEnemies(){
        val n = enemies.random()
        var enemy : Enemy ? = null
        repeat(n){
            val newEnemy = spawnEnemy()
            if(newEnemy!=null){
                enemy = newEnemy
            }
        }
        if(enemy!=null) {
            with(enemy!!.game.characterList.iterator()) {
                forEach {
                    if (it is Enemy) {
                        enemyList.add(it)
                    }
                }
            }
        }

    }
    fun spawnEnemy() : Enemy ?{
        val enemy = map.enemies.random().copy()
        val minMaxCoordinates = getMinMaxCoordinates()
        val xVal = (minMaxCoordinates.first.first + Math.random() * (minMaxCoordinates.second.first - minMaxCoordinates.first.first)).toFloat()
        val yVal = (minMaxCoordinates.first.second + Math.random() * (minMaxCoordinates.second.second - minMaxCoordinates.first.second)).toFloat()
        return if(map.inForbiddenArea(xVal,yVal) || !reachablePlayer(xVal,yVal,enemy.game)){
            spawnEnemy()
            null
        } else {
            enemy.spawn(xVal,yVal)
            enemy.smokeAnimation()
            enemy
        }
    }

    fun reachablePlayer(x: Float, y:Float, game: Game) : Boolean{
        val tileEnd = map.getMapIndexFromPosition(x,y)
        val tileStart = map.getMapIndexFromPosition(
            game.controllableCharacter!!.sprite.x,
            game.controllableCharacter!!.sprite.y
        )
        val lastTileAvailable = game.controllableCharacter!!.getShortestPath(tileStart, tileEnd).last()
        return abs(lastTileAvailable.first - tileEnd.first)<=1 || abs(lastTileAvailable.second-tileEnd.second)<=1
    }

    open fun open(){
        open = true
        composition = when(exit){
            "top"-> composition.replace("8","C")
            "left"-> composition.replace("A","E")
            "bottom"-> composition.replace("9","D")
            else-> composition.replace("B","F")
        }
        map.reload()
    }

    fun enemiesAlive(game: Game) : Boolean{
        with(game.characterList.iterator()){
            forEach {
                if(it is Enemy && it.alive){
                    return true
                }
            }
        }
        return false
    }

    fun isOpenable(game: Game){
        if(this is BasicRoom || this is LargeRoom || this is LongRoom) {
            if (!enemiesAlive(game)) {
                open()
                endChallenge(game)
                enemyList = mutableListOf()
            }
        }
    }

    open fun close(){
        open = false
        composition = when(exit){
            "top"->composition.replace("C","8")
            "left"->composition.replace("E","A")
            "bottom"->composition.replace("D","9")
            else->composition.replace("F","B")
        }
        map.reload()
    }

    fun startChallenge(game: Game){
        challenge?.effect?.invoke(game)
        game.challenge.value = challenge
    }

    fun endChallenge(game: Game){
        challenge?.reverseEffect?.invoke(game)
    }






}