package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder
import java.util.LinkedList
import java.util.Queue
import kotlin.reflect.KClass
import kotlin.math.log

open class Room(val row: Int, val col: Int, val map: Map, var enter: String ?= null, var exit : String ?= null, var open : Boolean = false, val enemies : IntRange) {

    var composition : String = create().trimIndent()
    var topLeftCorner : Pair<Int,Int> ?= null
    var bottomRightCorner : Pair<Int,Int> ?= null
    var enemyList : MutableList<Enemy> = mutableListOf()

    open fun copy() : Room{
        return Room(row, col, map, enter, exit, open, enemies)
    }

    fun randomTile(): Char {
        val tiles = listOf('!', '!', '!', '!', '!', '!', '!', '!', '!', '_')
        val randInd = (0 until tiles.size).random()
        return tiles[randInd]
    }
    open fun create() : String {
        val theMap = StringBuilder()
        for (i in 1..row) {
            when (i) {
                1 -> if (exit=="top" || enter=="top") {
                        if(exit=="top" && open){
                            theMap.append("0122222U3333345")
                        } else {
                            theMap.append("0122222O3333345")
                        }
                } else {
                    theMap.append("012222233333345")
                }

                4 -> for (j in 1..col) {
                    when (j) {
                        1 -> if (exit=="left" || enter=="left") {
                            if(exit=="left" && open){
                                theMap.append('W')
                            } else {
                                theMap.append('Q')
                            }
                        } else {
                            theMap.append('I')
                        }

                        col -> if (exit=="right" || enter=="right") {
                            if(exit=="right" && open){
                                theMap.append('X')
                            } else {
                                theMap.append('R')
                            }

                        } else {
                            theMap.append('L')
                        }

                        else -> theMap.append(randomTile())
                    }
                }

                row -> if(exit=="bottom" || enter=="bottom"){
                    if(exit=="bottom" && open){
                        theMap.append("6788888V99999AB")
                    } else {
                        theMap.append("6788888P99999AB")
                    }

                } else {
                    theMap.append("6788888899999AB")
                }
                else -> for (j in 1..col) {
                    when (j) {
                        1 -> if (i == 2) {
                            theMap.append('C')
                        } else if (i == 3) {
                            theMap.append('I')
                        } else if (i == 5) {
                            theMap.append('E')
                        } else if (i == 6) {
                            theMap.append('K')
                        }

                        col -> if (i == 2) {
                            theMap.append('F')
                        } else if (i == 3) {
                            theMap.append('L')
                        } else if (i == 5) {
                            theMap.append('D')
                        } else if (i == 6) {
                            theMap.append('J')
                        }

                        else -> theMap.append(randomTile())
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

    fun findStartPosition(map: List<List<Char>>): Pair<Int, Int>? {

        val doorStart = when (enter) {
            "top" -> {
                'O'
            }
            "left" -> {
                'Q'
            }
            "right" -> {
                'R'
            }
            else -> {
                'P'
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

    fun findEndPosition(map: List<List<Char>>): Pair<Int, Int>? {

        val doorEnd =  when(open){
            true -> when (exit) {
                "top" -> {
                    'U'
                }
                "left" -> {
                    'W'
                }
                "right" -> {
                    'X'
                }
                else -> {
                    'V'
                }
            }
            else->when (exit){
                "top"->'O'
                "left"->'Q'
                "right"->'R'
                else->'P'
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

    fun toList() : MutableList<MutableList<String>>{
        val list = composition.split("\n").map {
            it.split("").toMutableList()
        }.toMutableList()
        with(list.iterator()){
            forEach {
                it.removeAll(listOf(""))
            }
        }
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
        return toList()[localPosition.first][localPosition.second]
    }

    fun placeCharacter(game: Game){
        val roomList = toList()

        val startPosition = findStartPosition(roomList.map {
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

    fun getMinMaxCoordinates() : Pair<Pair<Float,Float>,Pair<Float,Float>>{
        return Pair(
            map.getPositionFromMapIndex(topLeftCorner!!.first+1, topLeftCorner!!.second+1),
            map.getPositionFromMapIndex(bottomRightCorner!!.first-1, bottomRightCorner!!.second-1)
        )
    }

    fun inMinMaxCoordinates(x: Float, y:Float) : Boolean{
        val minMaxCoordinates = getMinMaxCoordinates()
        Log.i("Min Max coordinates","$minMaxCoordinates")
        Log.i("x,y","$x,$y")
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
        with(enemy!!.game.characterList.iterator()){
            forEach {
                if(it!=null && it is Enemy){
                    enemyList.add(it)
                }
            }
        }
    }
    fun spawnEnemy() : Enemy ?{
        val enemy = map.enemies.random().copy()
        val minMaxCoordinates = getMinMaxCoordinates()
        val xVal = (minMaxCoordinates.first.first + Math.random() * (minMaxCoordinates.second.first - minMaxCoordinates.first.first)).toFloat()
        val yVal = (minMaxCoordinates.first.second + Math.random() * (minMaxCoordinates.second.second - minMaxCoordinates.first.second)).toFloat()
        return if(map.inForbiddenArea(xVal,yVal)){
            spawnEnemy()
            null
        } else {
            enemy.spawn(xVal,yVal)
            enemy.smokeAnimation()
            enemy
        }
    }

    fun open(){
        open = true
        composition = when(exit){
            "top"-> composition.replace("O","U")
            "left"-> composition.replace("Q","W")
            "bottom"-> composition.replace("P","V")
            else-> composition.replace("R","X")
        }
        map.reload()
    }

    fun enemiesAlive() : Boolean{
        for(enemy in enemyList){
            if(enemy.alive){
                return true
            }
        }
        return false
    }

    fun isOpenable(){
        if(!enemiesAlive()){
            open()
            enemyList = mutableListOf()
        }
    }

    fun close(){
        open = false
        composition = when(exit){
            "top"->composition.replace("U","O")
            "left"->composition.replace("W","Q")
            "bottom"->composition.replace("V","P")
            else->composition.replace("X","R")
        }
        map.reload()
    }




}