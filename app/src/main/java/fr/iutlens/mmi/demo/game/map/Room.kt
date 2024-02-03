package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder
import java.util.LinkedList
import java.util.Queue
import kotlin.math.log

open class Room(val row: Int, val col: Int, val map: Map, var enter: String ?= null, var exit : String ?= null, var open : Boolean = false) {

    var composition : String = create().trimIndent()
    var topLeftCorner : Pair<Int,Int> ?= null
    var bottomRightCorner : Pair<Int,Int> ?= null

    open fun copy() : Room{
        return Room(row, col, map, enter, exit, open)
    }

    fun randomTile(): Char {
        val tiles = listOf('!', '!', '!', '!', '!', '!', '!', '!', '!', '_')
        val randInd = (0 until tiles.size).random()
        return tiles[randInd]
    }
    open fun create() : String {
        val theMap = StringBuilder()
        Log.i("testfinal2", enter.toString())
        Log.i("testfinal3", exit.toString())

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



        val result = isPathAvailable(mapChars)

        if (!result) {
            return create()
        }

        return theMap.toString()
    }


    fun isPathAvailable(map: List<List<Char>>): Boolean {
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        val visited = mutableSetOf<Pair<Int, Int>>()

        val start = findStartPosition(map)
        val end = findEndPosition(map)

        if (start == null || end == null) {
            return false
        }

        queue.offer(start)
        visited.add(start)

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
                map[row][col] != '!' && map[row][col] != '_'

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
                    return Pair(i, j)
                }
            }
        }
        return null
    }

    fun findEndPosition(map: List<List<Char>>): Pair<Int, Int>? {

        val doorEnd = when (exit) {
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

        for (i in map.indices) {
            for (j in map[i].indices) {
                if (map[i][j] == doorEnd) {
                    return Pair(i, j)
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
        return Pair(
            row-topLeftCorner!!.first,
            column-topLeftCorner!!.second
        )
    }

    fun getElement(x: Float, y: Float) : String{
        val globalPosition = map.getMapIndexFromPosition(x,y)
        val localPosition = getPosition(globalPosition.first, globalPosition.second)
        return toList()[localPosition.first][localPosition.second]
    }

    fun placeCharacter(game: Game){
        game.controllableCharacter!!.sprite.x = getRoomCenter().first
        game.controllableCharacter!!.sprite.y = getRoomCenter().second
    }




}