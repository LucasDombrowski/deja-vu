package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import java.lang.StringBuilder

class TreasureRoom(enter : String ?=null, exit : String?=null, map: Map) : Room(
    row = 7,
    col = 15,
    open = true,
    enter = enter,
    exit = exit,
    map = map,
    enemies = 0..0
){
    override fun create() : String {
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

                        else -> theMap.append("!")
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

                        else -> theMap.append('!')
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

    override fun copy() : TreasureRoom{
        return TreasureRoom(enter,exit,map)
    }
}