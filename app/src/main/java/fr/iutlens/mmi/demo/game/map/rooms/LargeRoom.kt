package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder

class LargeRoom(val enterSide: String, val exitSide: String, enter: String ?=null, exit: String ?=null, map: Map) : Room(row = 14, col = 15, map,enter,exit,false, enemies =  4..6){

    var firstHalf = ""
    var secondHalf = ""

    override fun create() : String{
        firstHalf = createFirstHalf().trimIndent()
        secondHalf = createSecondHalf().trimIndent()
        if(enter!=null || exit!=null) {
            val roomList = toList()
            val result = isPathAvailable(roomList.map {
                it.map {
                    it.single()
                }.toList()
            }.toList())
            if (!result) {
                return create()
            }
        }
        return firstHalf+secondHalf
    }

    fun createFirstHalf() : String {
        val theMap = StringBuilder()
        for(i in 0..<row/2){
            when(i){
                0->if(enterSide!="top") {
                    for (j in 0..<col) {
                        when(j){
                            0->theMap.append("I")
                            col-1->theMap.append("L")
                            else->theMap.append(randomTile())
                        }
                    }
                } else {
                    for (j in 0..<col) {
                        when (j) {
                            0 -> theMap.append("0")
                            1 -> theMap.append("1")
                            (col - 1) / 2 -> {
                                if (enter == "top") {
                                    theMap.append("O")
                                } else {
                                    theMap.append("2")
                                }
                            }
                            col - 2 -> theMap.append("4")
                            col - 1 -> theMap.append("5")
                            else -> theMap.append("2")
                        }
                    }
                }
                (row/2)-1->if(enterSide!="bottom") {
                    for (j in 0..<col) {
                        when(j){
                            0->theMap.append("I")
                            col-1->theMap.append("L")
                            else->theMap.append(randomTile())
                        }
                    }
                } else {
                    for (j in 0..<col) {
                        when (j) {
                            0 -> theMap.append("6")
                            1 -> theMap.append("7")
                            (col - 1) / 2 -> {
                                if (enter == "bottom") {
                                    theMap.append("P")
                                } else {
                                    theMap.append("8")
                                }
                            }
                            col - 2 -> theMap.append("A")
                            col - 1 -> theMap.append("B")
                            else -> theMap.append("8")
                        }
                    }
                }
                else->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                when(i){
                                    1->theMap.append("C")
                                    (row/2)-2->theMap.append("K")
                                    ((row/2)-1)/2->if(enter=="left"){
                                        theMap.append("Q")
                                    } else {
                                        theMap.append("I")
                                    }
                                    else->theMap.append("I")
                                }
                            }
                            col-1->{
                                when(i){
                                    1->theMap.append("F")
                                    (row/2)-2->theMap.append("J")
                                    ((row/2)-1)/2->if(enter=="right"){
                                        theMap.append("R")
                                    } else {
                                        theMap.append("L")
                                    }
                                    else->theMap.append("L")
                                }
                            }
                            else->theMap.append(randomTile())
                        }
                    }
                }
            }
            theMap.appendLine()
        }
        return theMap.toString()
    }

    fun createSecondHalf() : String {
        val theMap = StringBuilder()
        for(i in 0..<row/2){
            when(i){
                0->if(exitSide!="top") {
                    for (j in 0..<col) {
                        when(j){
                            0->theMap.append("I")
                            col-1->theMap.append("L")
                            else->theMap.append(randomTile())
                        }
                    }
                } else {
                    for (j in 0..<col) {
                        when (j) {
                            0 -> theMap.append("0")
                            1 -> theMap.append("1")
                            (col - 1) / 2 -> {
                                if (exit == "top") {
                                    theMap.append("O")
                                } else {
                                    theMap.append("2")
                                }
                            }
                            col - 2 -> theMap.append("4")
                            col - 1 -> theMap.append("5")
                            else -> theMap.append("2")
                        }
                    }
                }
                (row/2)-1->if(exitSide!="bottom") {
                    for (j in 0..<col) {
                        when(j){
                            0->theMap.append("I")
                            col-1->theMap.append("L")
                            else->theMap.append(randomTile())
                        }
                    }
                } else {
                    for (j in 0..<col) {
                        when (j) {
                            0 -> theMap.append("6")
                            1 -> theMap.append("7")
                            (col - 1) / 2 -> {
                                if (exit == "bottom") {
                                    theMap.append("P")
                                } else {
                                    theMap.append("8")
                                }
                            }
                            col - 2 -> theMap.append("A")
                            col - 1 -> theMap.append("B")
                            else -> theMap.append("8")
                        }
                    }
                }
                else->{
                    for(j in 0..<col){
                        when(j){
                            0->{
                                when(i){
                                    1->theMap.append("C")
                                    (row/2)-2->theMap.append("K")
                                    ((row/2)-1)/2->if(exit=="left"){
                                        theMap.append("Q")
                                    } else {
                                        theMap.append("I")
                                    }
                                    else->theMap.append("I")
                                }
                            }
                            col-1->{
                                when(i){
                                    1->theMap.append("F")
                                    (row/2)-2->theMap.append("J")
                                    ((row/2)-1)/2->if(exit=="right"){
                                        theMap.append("R")
                                    } else {
                                        theMap.append("L")
                                    }
                                    else->theMap.append("L")
                                }
                            }
                            else->theMap.append(randomTile())
                        }
                    }
                }
            }
            theMap.appendLine()
        }
        return theMap.toString()
    }

    override fun refresh(){
        create()
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
        val list = secondHalf.split("\n").map {
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
        val roomList = when(enterSide){
            "top"->(firstHalfList().toMutableList() + secondHalfList().toMutableList()).toMutableList()
            else->(secondHalfList().toMutableList() + firstHalfList().toMutableList()).toMutableList()
        }
        return roomList
    }

    override fun findStartPosition(map: List<List<Char>>): Pair<Int, Int>? {
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
        when(enterSide){
            "top"->{
                for(i in 0..<map.size/2){
                    for(j in map[i].indices){
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
            }
            else->{
                for(i in map.size/2..<map.size){
                    for(j in map[i].indices){
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
            }
        }
        return null
    }

    override fun findEndPosition(map: List<List<Char>>): Pair<Int, Int>? {
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
        when(enterSide){
            "bottom"->{
                for(i in 0..<map.size/2){
                    for(j in map[i].indices){
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
            }
            else->{
                for(i in map.size/2..<map.size){
                    for(j in map[i].indices){
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
            }
        }
        return null
    }
}
