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
    override var composition = create(false).trimIndent()

    override fun copy() : TreasureRoom{
        return TreasureRoom(enter,exit,map)
    }

    override fun refresh(){
        composition = create(false).trimIndent()
        toList()
    }

    override fun create(obstacles: Boolean) : String {

        val theMap = StringBuilder()
        for(i in 0..<row){
            when(i){
                0->{
                    emptyLine(theMap)
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                theMap.append("0")
                            }
                            col-1->{
                                theMap.append("1")
                                theMap.append("K")
                            }
                            (col-1)/2->{
                                if(enter=="top"){
                                    theMap.append(door(true))
                                } else if(exit=="top"){
                                    theMap.append(door(false))
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
                            0->{
                                theMap.append("K")
                                theMap.append("4")
                            }
                            (col-1)/2->{
                                if(enter=="bottom"){
                                    theMap.append(door(true))
                                } else if(exit=="bottom"){
                                    theMap.append(door(false))
                                } else {
                                    theMap.append("7")
                                }
                            }
                            col-1->{
                                theMap.append("5")
                                theMap.append("K")
                            }
                            else->theMap.append("7")
                        }
                    }
                }
                ((row-1)/2)-1->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="left"){
                                            theMap.append(door(true))
                                        } else if(exit=="left"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("6")
                                        }
                                    }
                                    else->theMap.append("6")
                                }
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("3")
                                        }
                                    }
                                    else->theMap.append("3")
                                }
                                theMap.append("K")
                            }
                            ((col-1)/2)-1->theMap.append("%")
                            (col-1)/2->theMap.append("¨")
                            ((col-1)/2)+1->theMap.append("µ")
                            else->theMap.append(randomTile(obstacles))
                        }
                    }
                }
                (row-1)/2->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="left"){
                                            theMap.append(door(true))
                                        } else if(exit=="left"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("6")
                                        }
                                    }
                                    else->theMap.append("6")
                                }
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("3")
                                        }
                                    }
                                    else->theMap.append("3")
                                }
                                theMap.append("K")
                            }
                            ((col-1)/2)-1->theMap.append("?")
                            (col-1)/2->theMap.append("~")
                            ((col-1)/2)+1->theMap.append("£")
                            else->theMap.append(randomTile(obstacles))
                        }
                    }
                }
                ((row-1)/2)+1->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="left"){
                                            theMap.append(door(true))
                                        } else if(exit=="left"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("6")
                                        }
                                    }
                                    else->theMap.append("6")
                                }
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("3")
                                        }
                                    }
                                    else->theMap.append("3")
                                }
                                theMap.append("K")
                            }
                            ((col-1)/2)-1->theMap.append("/")
                            (col-1)/2->theMap.append(".")
                            ((col-1)/2)+1->theMap.append("§")
                            else->theMap.append(randomTile(obstacles))
                        }
                    }
                }
                else->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                theMap.append("K")
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="left"){
                                            theMap.append(door(true))
                                        } else if(exit=="left"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("6")
                                        }
                                    }
                                    else->theMap.append("6")
                                }
                            }
                            col-1->{
                                when(i){
                                    (row-1)/2->{
                                        if(enter=="right"){
                                            theMap.append(door(true))
                                        }  else if(exit=="right"){
                                            theMap.append(door(false))
                                        } else {
                                            theMap.append("3")
                                        }
                                    }
                                    else->theMap.append("3")
                                }
                                theMap.append("K")
                            }
                            else->theMap.append(randomTile(obstacles))
                        }
                    }
                }
            }
            theMap.appendLine()
        }
        emptyLine(theMap)

        return theMap.toString()
    }

}