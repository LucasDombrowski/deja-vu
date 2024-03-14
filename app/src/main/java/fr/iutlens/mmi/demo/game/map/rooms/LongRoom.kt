package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Challenge
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.utils.getCenter
import java.lang.StringBuilder

class LongRoom(val enterSide: String, val exitSide: String, enter: String ?=null, exit: String ?=null, map: Map, challenge: Challenge) : Room(row = 7, col = 30, map,enter,exit,false, enemies =  4..6, challenge = challenge){

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
        for(i in 0..<row){
            when(i){
                0->{
                    for(j in 0..<col/2){
                        when(j){
                            0->if(enterSide=="left"){
                                theMap.append("0")
                            } else {
                                theMap.append("2")
                            }
                            ((col/2)-1)/2->{
                                if(enter=="top"){
                                    theMap.append("8")
                                } else {
                                    theMap.append("2")
                                }
                            }
                            (col/2)-1->if(enterSide=="right"){
                                theMap.append("1")
                            } else {
                                theMap.append("2")
                            }
                            else->theMap.append("2")
                        }
                    }
                }
                row-1->{
                    for(j in 0..<col/2){
                        when(j){
                            0->if(enterSide=="left"){
                                theMap.append("4")
                            } else {
                                theMap.append("7")
                            }
                            ((col/2)-1)/2->{
                                if(enter=="bottom"){
                                    theMap.append("9")
                                } else {
                                    theMap.append("7")
                                }
                            }
                            (col/2)-1->if(enterSide=="right"){
                                theMap.append("5")
                            } else {
                                theMap.append("7")
                            }
                            else->theMap.append("7")
                        }
                    }
                }
                else->{
                    for(j in 0..<col/2){
                        when(j){
                            0->{
                                if(enterSide!="left"){
                                    theMap.append(randomTile())
                                } else {
                                    when(i){
                                        (row-1)/2->if(enter=="left"){
                                            theMap.append("A")
                                        } else {
                                            theMap.append("6")
                                        }
                                        else->theMap.append("6")
                                    }
                                }
                            }
                            (col/2)-1->{
                                if(enterSide!="right"){
                                    theMap.append(randomTile())
                                } else {
                                    when(i){
                                        (row-1)/2->if(enter=="right"){
                                            theMap.append("B")
                                        } else {
                                            theMap.append("3")
                                        }
                                        else->theMap.append("3")
                                    }
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
        for(i in 0..<row){
            when(i){
                0->{
                    for(j in 0..<col/2){
                        when(j){
                            0->if(exitSide=="left"){
                                theMap.append("0")
                            } else {
                                theMap.append("2")
                            }
                            ((col/2)-1)/2->{
                                if(exit=="top"){
                                    theMap.append("8")
                                } else {
                                    theMap.append("2")
                                }
                            }
                            (col/2)-1->if(exitSide=="right"){
                                theMap.append("1")
                            } else {
                                theMap.append("2")
                            }
                            else->theMap.append("2")
                        }
                    }
                }
                row-1->{
                    for(j in 0..<col/2){
                        when(j){
                            0->if(exitSide=="left"){
                                theMap.append("4")
                            } else {
                                theMap.append("7")
                            }
                            ((col/2)-1)/2->{
                                if(exit=="bottom"){
                                    theMap.append("9")
                                } else {
                                    theMap.append("7")
                                }
                            }
                            (col/2)-1->if(exitSide=="right"){
                                theMap.append("5")
                            } else {
                                theMap.append("7")
                            }
                            else->theMap.append("7")
                        }
                    }
                }
                else->{
                    for(j in 0..<col/2){
                        when(j){
                            0->{
                                if(exitSide!="left"){
                                    theMap.append(randomTile())
                                } else {
                                    when(i){
                                        (row-1)/2->if(exit=="left"){
                                            theMap.append("A")
                                        } else {
                                            theMap.append("6")
                                        }
                                        else->theMap.append("6")
                                    }
                                }
                            }
                            (col/2)-1->{
                                if(exitSide!="right"){
                                    theMap.append(randomTile())
                                } else {
                                    when(i){
                                        (row-1)/2->if(exit=="right"){
                                            theMap.append("B")
                                        } else {
                                            theMap.append("3")
                                        }
                                        else->theMap.append("3")
                                    }
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


    fun getFirstHalfCenter() : Pair<Float,Float>{
        val roomCenter = getRoomCenter()
        val minMaxPos = getMinMaxCoordinates()
        val topLeftCornerFloatPos = map.getPositionFromMapIndex(topLeftCorner!!.first, topLeftCorner!!.second)
        val bottomRightCornerFloatPos = Pair(
            roomCenter.first,
            minMaxPos.second.second
        )
        val center = getCenter(topLeftCornerFloatPos.first, topLeftCornerFloatPos.second, bottomRightCornerFloatPos.first, bottomRightCornerFloatPos.second)
        return Pair(center[0],center[1])
    }

    fun getSecondHalfCenter() : Pair<Float,Float>{
        val roomCenter = getRoomCenter()
        val minMaxPos = getMinMaxCoordinates()
        val bottomRightCornerFloatPos = map.getPositionFromMapIndex(bottomRightCorner!!.first, bottomRightCorner!!.second)
        val topLeftCornerFloatPos = Pair(
            roomCenter.first,
            minMaxPos.first.second
        )
        val center = getCenter(topLeftCornerFloatPos.first, topLeftCornerFloatPos.second, bottomRightCornerFloatPos.first, bottomRightCornerFloatPos.second)
        return Pair(center[0],center[1])
    }

    override fun toList(): MutableList<MutableList<String>> {
        val list = mutableListOf<MutableList<String>>()
        for(i in 0..<row){
            when(enterSide){
                "left"->list.add((firstHalfList()[i]+secondHalfList()[i]).toMutableList())
                else->list.add((secondHalfList()[i]+firstHalfList()[i]).toMutableList())
            }
        }
        roomList = list
        return list
    }

    override fun findStartPosition(map: List<List<Char>>): Pair<Int, Int>? {
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
        when(enterSide){
            "left"->{
                for(i in map.indices){
                    for(j in 0..<map[i].size/2){
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
                for(i in map.indices){
                    for(j in map[i].size/2..<map[i].size){
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
        when(enterSide){
            "right"->{
                for(i in map.indices){
                    for(j in 0..<map[i].size/2){
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
                for(i in map.indices){
                    for(j in map[i].size/2..<map[i].size){
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

    override fun open(){
        open = true
        secondHalf = when(exit){
            "top"-> secondHalf.replace("8","C")
            "left"-> secondHalf.replace("A","E")
            "bottom"-> secondHalf.replace("9","D")
            else-> secondHalf.replace("B","F")
        }
        map.reload()
    }

    override fun close(){
        open = false
        secondHalf = when(exit){
            "top"-> secondHalf.replace("C","8")
            "left"-> secondHalf.replace("E","A")
            "bottom"-> secondHalf.replace("D","9")
            else-> secondHalf.replace("F","B")
        }
        map.reload()
    }

}
