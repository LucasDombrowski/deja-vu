package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import java.lang.StringBuilder

open class StartingRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit, enemies = 0..0) {
    override fun create(): String {
        val theMap = StringBuilder()
        for(i in 0..<row){
            when(i){
                0->{
                    for(j in 0..<col){
                        when(j){
                            0->theMap.append("0")
                            col-1->theMap.append("1")
                            (col-1)/2->{
                                if(exit=="top"){
                                    theMap.append("C")
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
                                if(exit=="bottom"){
                                    theMap.append("D")
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
                                    if(exit == "left"){
                                        theMap.append("E")
                                    } else {
                                        theMap.append("6")
                                    }
                                }
                                else->theMap.append("6")
                            }
                            col-1->when(i){
                                (row-1)/2->{
                                    if(exit=="right"){
                                        theMap.append("F")
                                    } else {
                                        theMap.append("3")
                                    }
                                }
                                else->theMap.append("3")
                            }
                            else->theMap.append(randomTile(false))
                        }
                    }
                }
            }
            theMap.appendLine()
        }

        return theMap.toString()
    }

    override fun copy(): Room {
        return StartingRoom(map, enter, exit)
    }
}