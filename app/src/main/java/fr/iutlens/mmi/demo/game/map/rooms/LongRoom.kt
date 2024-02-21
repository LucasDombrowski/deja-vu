package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import java.lang.StringBuilder

class LongRoom(val enterSide: String, val exitSide: String, enter: String ?=null, exit: String ?=null, map: Map) : Room(row = 7, col = 30, map,enter,exit,false, enemies =  4..6){

    var firstHalf = createFirstHalf().trimIndent()
    var secondHalf = createSecondHalf().trimIndent()
    fun createFirstHalf() : String {
        val theMap = StringBuilder()
        for (i in 1..row) {
            when (i) {
                1 -> if(enter=="top"){
                    theMap.append("0122222O3333345")
                } else {
                    theMap.append("012222233333345")
                }

                4 -> for (j in 1..col/2) {
                    when (j) {
                        1 -> if(enter=="left"){
                            theMap.append('Q')
                        } else {
                            theMap.append('I')
                        }

                        col/2 -> if(enter=="right"){
                            theMap.append('R')
                        } else {
                            theMap.append('L')
                        }
                        else -> theMap.append(randomTile())
                    }
                }

                row -> if(enter=="bottom"){
                    theMap.append("6788888P99999AB")
                } else {
                    theMap.append("6788888899999AB")
                }

                else -> for (j in 1..col/2) {
                    if (enterSide=="left"){
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

                            else -> theMap.append(randomTile())
                        }
                    }else{
                        when (j) {
                        col/2 -> if (i == 2) {
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
        for (i in 1..row) {
            when (i) {
                1 -> if (exit=="top") {
                    if(open){
                        theMap.append("0122222U3333345")
                    } else {
                        theMap.append("0122222O3333345")
                    }
                } else {
                    theMap.append("012222233333345")
                }

                4 -> for (j in 1..col/2) {
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

                        col/2 -> if (exit=="right") {
                            if(open) {
                                theMap.append('X')
                            }
                            } else {
                                theMap.append('L')
                            }
                        else -> theMap.append(randomTile())
                    }
                }

                row -> if(exit=="bottom") {
                    if (open) {
                        theMap.append("6788888V99999AB")
                    } else {
                        theMap.append("6788888899999AB")
                    }
                }

                else -> for (j in 1..col/2) {
                    if (exitSide=="right"){
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

                            else -> theMap.append(randomTile())
                        }
                    }else{
                        when (j) {
                            col/2 -> if (i == 2) {
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
}
