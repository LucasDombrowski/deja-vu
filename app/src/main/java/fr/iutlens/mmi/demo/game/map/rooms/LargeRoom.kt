package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder

class LargeRoom(val enterSide: String, val exitSide: String, enter: String ?=null, exit: String ?=null, map: Map) : Room(row = 14, col = 15, map,enter,exit,false, enemies =  4..6){

    var firstHalf = createFirstHalf().trimIndent()
    var secondHalf = createSecondHalf().trimIndent()
    fun createFirstHalf() : String {
        val theMap = StringBuilder()
        for (i in 1..row/2) {
            when (i) {
                1 -> if(enter=="top"){
                    theMap.append("0122222O3333345")
                } else {
                    if(enterSide=="bottom"){
                        theMap.append("0!!!!!!!!!!!!!5")
                    }else {
                        theMap.append("012222233333345")
                    }
                }

                4 -> for (j in 1..col) {
                    when (j) {
                        1 -> if(enter=="left"){
                            theMap.append('Q')
                        } else {
                            theMap.append('I')
                        }

                        col -> if(enter=="right"){
                            theMap.append('R')
                        } else {
                            theMap.append('L')
                        }
                        else -> theMap.append(randomTile())
                    }
                }

                row/2 -> if(enter=="bottom"){
                    theMap.append("6788888P99999AB")
                } else {
                    if(enterSide=="top"){
                        theMap.append("6!!!!!!!!!!!!!B")
                    }else {
                        theMap.append("6788888899999AB")
                    }
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
        return theMap.toString()
    }

    fun createSecondHalf() : String {
        val theMap = StringBuilder()
        for (i in 1..row/2) {
            when (i) {
                1 -> if(exit=="top"){
                    theMap.append("0122222O3333345")
                } else {
                    if(exitSide=="bottom"){
                        theMap.append("0!!!!!!!!!!!!!5")
                    }else {
                        theMap.append("012222233333345")
                    }
                }

                4 -> for (j in 1..col) {
                    when (j) {
                        1 -> if (exit=="left") {
                            if(open){
                                theMap.append('W')
                            } else {
                                theMap.append('Q')
                            }
                        } else {
                            theMap.append('I')
                        }

                        col -> if (exit=="right") {
                            if(open) {
                                theMap.append('X')
                            }
                        } else {
                            theMap.append('L')
                        }
                        else -> theMap.append(randomTile())
                    }
                }

                row/2 -> if(exit=="bottom"){
                    theMap.append("6788888P99999AB")
                } else {
                    if(exitSide=="top"){
                        theMap.append("6!!!!!!!!!!!!!B")
                    }else {
                        theMap.append("6788888899999AB")
                    }
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
        return theMap.toString()
    }

    override fun refresh(){
        firstHalf = createFirstHalf().trimIndent()
        secondHalf = createSecondHalf().trimIndent()
    }

    fun getFirstHalfCenter() : Pair<Float,Float>{
        val roomCenter = getRoomCenter()
        val minMaxPos = getMinMaxCoordinates()
        val topLeftCornerFloatPos = map.getPositionFromMapIndex(topLeftCorner!!.first, topLeftCorner!!.second)
        val bottomRightCornerFloatPos = Pair(
            minMaxPos.second.first,
            roomCenter.second
        )
        val center = getCenter(topLeftCornerFloatPos.first, topLeftCornerFloatPos.second, bottomRightCornerFloatPos.first, bottomRightCornerFloatPos.second)
        return Pair(center[0],center[1])
    }

    fun getSecondHalfCenter() : Pair<Float,Float>{
        val roomCenter = getRoomCenter()
        val minMaxPos = getMinMaxCoordinates()
        val bottomRightCornerFloatPos = map.getPositionFromMapIndex(bottomRightCorner!!.first, bottomRightCorner!!.second)
        val topLeftCornerFloatPos = Pair(
            minMaxPos.first.first,
            roomCenter.second
        )
        val center = getCenter(topLeftCornerFloatPos.first, topLeftCornerFloatPos.second, bottomRightCornerFloatPos.first, bottomRightCornerFloatPos.second)
        return Pair(center[0],center[1])
    }

    fun firstHalfList() : MutableList<MutableList<String>>{
        val list = firstHalf.split("\n").map {
            it.split("").toMutableList()
        }.toMutableList()
        with(list.iterator()){
            forEach {
                it.removeAll(listOf(""))
            }
        }
        return list
    }

    fun secondHalfList() : MutableList<MutableList<String>>{
        val list = firstHalf.split("\n").map {
            it.split("").toMutableList()
        }.toMutableList()
        with(list.iterator()){
            forEach {
                it.removeAll(listOf(""))
            }
        }
        return list
    }


    override fun toList(): MutableList<MutableList<String>> {
        return (firstHalfList() + secondHalfList()).toMutableList()
    }
}
