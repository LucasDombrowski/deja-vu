package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.rooms.BasicRoom
import fr.iutlens.mmi.demo.game.map.rooms.BossRoom
import fr.iutlens.mmi.demo.game.map.rooms.LargeRoom
import fr.iutlens.mmi.demo.game.map.rooms.LongRoom
import fr.iutlens.mmi.demo.game.map.rooms.ShopRoom
import fr.iutlens.mmi.demo.game.map.rooms.StartingRoom
import fr.iutlens.mmi.demo.game.map.rooms.TreasureRoom
import fr.iutlens.mmi.demo.game.sprite.ArrayTileMap
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.game.sprite.sprites.Boss
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import java.util.BitSet
import kotlin.collections.Map

open class Map(val roomInterval: IntRange, val drawable: Int, var enemies: List<Enemy> = listOf(), val treasureRooms : Int, var boss : Boss ? = null) {

    val authorizedTiles : List<String> = listOf("!","U","V","W","X")
    var roomNumber = roomInterval.random()
    var tileMap = makeTileMap()
    var tileArea = makeTileArea()
    var mapString : String ? = null
    var mapPath : MutableList<MutableList<String>> ?= null
    var rooms : MutableList<Room> ?= null
    var roomSequence : List<String>? = null
    var currentRoom = 0

    fun makeTileMap() : ArrayTileMap{
        if(mapString==null) {
            generateMap()
            Log.i("mapString","$mapString")
        }

        return mapString!!.trimIndent().toMutableTileMap(
            "012345"+
                    "6789AB"+
                    "CDEFGH" +
                    "IJKLMN" +
                    "OPQRST" +
                    "UVWXYZ" +
                    "!-*/=_"
        )
    }

    fun makeTileArea(): TiledArea {
        return drawable.tiledArea(tileMap)
    }
    fun getMapIndexFromPosition(x: Float, y: Float) : Pair<Int,Int>{
        var row = ((y)/tileArea.h).toInt()
        if(row<0){
            row=0
        } else if(row>=tileArea.sizeY){
            row = tileArea.sizeY-1
        }
        var column = ((x)/tileArea.w).toInt()
        if(column<0){
            column=0
        } else if(column>=tileArea.sizeX){
            column = tileArea.sizeX-1
        }
        return Pair(row,column)
    }

   fun getPositionFromMapIndex(x: Int, y: Int) : Pair<Float, Float>{
       return Pair(
           ((y)*tileArea.h).toFloat(),
           ((x)*tileArea.w).toFloat()
       )
   }

   fun getRoomFromMapIndex(row: Int, col: Int) : Room ?{
       with(rooms!!.iterator()){
           forEach {
               if(it.topLeftCorner!!.first<=row && row<=it.bottomRightCorner!!.first && it.topLeftCorner!!.second<=col && col<=it.bottomRightCorner!!.second){
                   return it
               }
           }
       }
       return null
   }

    fun inForbiddenArea(x: Float, y:Float) : Boolean{
        val globalPosition = getMapIndexFromPosition(x,y)
        val room = getRoomFromMapIndex(globalPosition.first, globalPosition.second)
        return room!!.getElement(x,y) !in authorizedTiles
    }
    fun generateMapPath() : MutableList<MutableList<String>>{
        var entranceDoor : String ?= null
        var n = 1
        var bigRooms = 0
        val sequence = mutableListOf<String>()
        val map = mutableListOf<MutableList<String>>()
        repeat(4*roomNumber){
            val row = mutableListOf<String>()
            repeat(4*roomNumber){
                row.add("")
            }
            map.add(row)
        }
        var currentRow = roomNumber
        var currentCol = roomNumber
        map[currentRow][currentCol] = n.toString()
        repeat(roomNumber){
            n+=1
            val mapChanges = generateNextRoom(map,currentRow,currentCol,entranceDoor,n.toString())
            currentRow = mapChanges["currentRow"] as Int
            currentCol = mapChanges["currentCol"] as Int
            entranceDoor = mapChanges["entranceDoor"] as String
            sequence.add(mapChanges["nextRoom"] as String)
            if((1..4).random() == 1 && it<roomNumber-1 && it>0 && bigRooms<treasureRooms+1){
                bigRooms++
                val mapChanges = generateNextRoom(map,currentRow,currentCol,entranceDoor,"$n.5")
                currentRow = mapChanges["currentRow"] as Int
                currentCol = mapChanges["currentCol"] as Int
                entranceDoor = mapChanges["entranceDoor"] as String
            }
        }
        fun isOnlyEmpty(row: List<String>) : Boolean{
            for(char in row){
                if(char!=""){
                    return false
                }
            }
            return true
        }
        roomSequence = sequence.filter {
            !it.contains(".5")
        }
        val filteredMap = map.filter {
            !isOnlyEmpty(it.toList())
        }
        val reducedMap = filteredMap.toMutableList()
        var col = 0
        while (col<reducedMap[0].size){
            var empty = true
            for(row in 0..<reducedMap.size){
                if(reducedMap[row][col]!=""){
                    empty=false
                }
            }
            if(empty){
                reducedMap.forEach {
                    it.removeAt(col)
                }
            } else {
                col++
            }
        }
        return reducedMap
    }

    fun generateMap(){
        if(mapPath==null){
            mapPath = generateMapPath()
        }
        if(rooms==null){
            generateRooms()
        }
        val mapList : MutableList<MutableList<String>> = mutableListOf()
        with(mapPath!!.iterator()){
            forEach {
                val row = mutableListOf<String>()
                with(it.iterator()){
                    forEach {
                        if(it==""){
                            row.add(fillEmptySpace(7, 15))
                        } else {
                            if(!it.contains(".5")){
                                val room = rooms!![it.toInt()-1]
                                if(findDoubleRoom(it.toInt())!=null){
                                    if(room is LargeRoom){
                                        row.add(room.firstHalf)
                                    }
                                    if(room is LongRoom){
                                        row.add(room.firstHalf)
                                    }
                                } else {
                                    row.add(room.composition)
                                }
                            } else {
                                val room = rooms!![it.replace(".5","").toInt()-1]
                                if(room is LargeRoom){
                                    row.add(room.secondHalf)
                                }
                                if(room is LongRoom){
                                    row.add(room.secondHalf)
                                }
                            }

                        }
                    }
                }

                mapList.add(row)
            }
        }
        val map = StringBuilder()
        with(mapList.iterator()){
            forEach {
                for(i in 0..6){
                    with(it.iterator()){
                        forEach {
                            map.append(it.split("\n")[i])
                        }
                    }
                    map.appendLine()
                }
            }
        }

        mapString = map.toString()

    }

    fun findRoom(n: Int) : Pair<Int,Int>?{
        for(i in 0..<mapPath!!.size){
            for(j in 0..<mapPath!![i].size){
                if(mapPath!![i][j]==n.toString()){
                    return Pair(i,j)
                }
            }
        }
        return null
    }

    fun secondHalfPosition(n : Int) : Pair<Int,Int> ?{
        for(i in 0..<mapPath!!.size){
            for(j in 0..<mapPath!![i].size){
                if(mapPath!![i][j]=="$n.5"){
                    return Pair(i,j)
                }
            }
        }
        return null
    }

    fun findDoubleRoom(n : Int) : Room ?{
        val pos = findRoom(n)!!
        if(pos.first<mapPath!!.size-1 && mapPath!![pos.first+1][pos.second] == "$n.5"){
            return LargeRoom(enterSide = "top", exitSide = "bottom", map = this)
        } else if(pos.first>0 && mapPath!![pos.first-1][pos.second] == "$n.5"){
            return LargeRoom(enterSide = "bottom", exitSide = "top", map = this)
        } else if(pos.second < mapPath!![pos.first].size-1 && mapPath!![pos.first][pos.second+1] == "$n.5"){
            return LongRoom(enterSide = "left", exitSide = "right", map = this)
        } else if(pos.second > 0 && mapPath!![pos.first][pos.second-1] == "$n.5"){
            return LongRoom(enterSide = "right", exitSide = "left", map = this)
        } else {
            return null
        }
    }


    fun generateRooms(){
        val currentMap = this
        var room : Room ? = BasicRoom(currentMap)
        val lastRoom = roomNumber+1
        val rowsToAdd = room!!.row
        val colsToAdd = room!!.col
        var currentRow = 0
        var currentCol = 0
        val roomList : MutableMap<String,Room> = mutableMapOf()
        val randomTreasureRooms = mutableListOf<String>()
        repeat(treasureRooms){
            var number = (2..roomNumber).random().toString()
            while(number in randomTreasureRooms || findDoubleRoom(number.toInt())!=null){
                number = (2..roomNumber).random().toString()
            }
            randomTreasureRooms.add(number)
        }
        var shopRoom = (2..roomNumber).random().toString()
        while(shopRoom in randomTreasureRooms || findDoubleRoom(shopRoom.toInt())!=null){
            shopRoom = (2..roomNumber).random().toString()
        }

        with(mapPath!!.iterator()){
            forEach {
                currentCol = 0
                with(it.iterator()){
                    forEach {
                         if(it!=""){
                             room = when(it){
                                 "1"->StartingRoom(currentMap)
                                 lastRoom.toString()->BossRoom(currentMap)
                                 in randomTreasureRooms->TreasureRoom(map = currentMap)
                                 shopRoom->ShopRoom(currentMap)
                                 else->{
                                     if(!it.contains(".5")){
                                         if(findDoubleRoom(it.toInt())!=null){
                                             findDoubleRoom(it.toInt())
                                         } else {
                                             BasicRoom(currentMap)
                                         }
                                     } else {
                                         null
                                     }
                                 }
                             }
                             if(room!=null) {
                                 val newRoom = room!!
                                 if(newRoom !is LongRoom && room !is LargeRoom) {
                                     newRoom.topLeftCorner = Pair(currentRow, currentCol)
                                     newRoom.bottomRightCorner = Pair(currentRow + rowsToAdd, currentCol + colsToAdd)
                                 } else{
                                     if(newRoom is LongRoom){
                                         if(newRoom.enterSide=="left"){
                                             newRoom.topLeftCorner = Pair(currentRow,currentCol)
                                             newRoom.bottomRightCorner = Pair(currentRow + rowsToAdd, currentCol + (colsToAdd*2))
                                         } else {
                                             newRoom.topLeftCorner = Pair(currentRow,currentCol-colsToAdd)
                                             newRoom.bottomRightCorner = Pair(currentRow+rowsToAdd,currentCol+colsToAdd)
                                         }
                                     }
                                     if(newRoom is LargeRoom){
                                         if(newRoom.enterSide=="top"){
                                             newRoom.topLeftCorner = Pair(currentRow,currentCol)
                                             newRoom.bottomRightCorner = Pair(currentRow+(rowsToAdd*2), currentCol+colsToAdd)
                                         } else {
                                             newRoom.topLeftCorner = Pair(currentRow-rowsToAdd,currentCol)
                                             newRoom.bottomRightCorner = Pair(currentRow+rowsToAdd,currentCol+colsToAdd)
                                         }
                                     }
                                 }
                                 roomList[it] = newRoom
                             }

                        }
                        currentCol+=colsToAdd
                    }
                }
                currentRow+=rowsToAdd
            }

        }
        rooms = roomList.toSortedMap().values.toMutableList()

        for(i in 0..<roomSequence!!.size){
            when{
                i==0->{
                    rooms!![i].exit = roomSequence!![i]
                    rooms!![i].open = true
                }
                else->{
                    rooms!![i].exit = roomSequence!![i]
                    rooms!![i].enter = getReverseDirection(roomSequence!![i-1])
                }
            }
            rooms!![i].refresh()
        }
        rooms!!.last().enter = getReverseDirection(roomSequence!!.last())
        rooms!!.last().refresh()
    }
    fun fillEmptySpace(row: Int, col: Int) : String{
        val emptyArea = StringBuilder()
        repeat(row){
            repeat(col){
                emptyArea.append("!")
            }
            emptyArea.appendLine()
        }
        return emptyArea.toString()
    }
    fun generateNextRoom(map : MutableList<MutableList<String>>, row: Int, col: Int, door: String ?= null, number: String, forbiddenDirections : MutableList<String> = mutableListOf()) : Map<String,Any>{
        var entranceDoor = door
        var currentCol = col
        var currentRow = row
        var nextRoom = when((1..4).random()){
            1->if(entranceDoor=="left"){
                "right"
            } else {
                "left"
            }
            2->if(entranceDoor=="top"){
                "bottom"
            } else {
                "top"
            }
            3->if(entranceDoor=="right"){
                "left"
            } else {
                "right"
            }
            else->if(entranceDoor=="bottom"){
                "top"
            } else {
                "bottom"
            }
        }
        while (nextRoom in forbiddenDirections) {
            nextRoom = when ((1..4).random()) {
                1 -> if (entranceDoor == "left") {
                    "right"
                } else {
                    "left"
                }

                2 -> if (entranceDoor == "top") {
                    "bottom"
                } else {
                    "top"
                }

                3 -> if (entranceDoor == "right") {
                    "left"
                } else {
                    "right"
                }

                else -> if (entranceDoor == "bottom") {
                    "top"
                } else {
                    "bottom"
                }
            }
        }
        when(nextRoom){
            "left"->if(map[currentRow][currentCol-1]!=""){
                        forbiddenDirections.add(nextRoom)
                        generateNextRoom(map,currentRow,currentCol,entranceDoor,number, forbiddenDirections)
                    } else {
                        currentCol-=1
                        map[currentRow][currentCol] = number
                    }
            "right"->if(map[currentRow][currentCol+1]!=""){
                        forbiddenDirections.add(nextRoom)
                        generateNextRoom(map,currentRow,currentCol,entranceDoor,number, forbiddenDirections)
                    } else {
                        currentCol+=1
                        map[currentRow][currentCol] = number
                    }
            "top"->if(map[currentRow-1][currentCol]!=""){
                        forbiddenDirections.add(nextRoom)
                        generateNextRoom(map,currentRow,currentCol,entranceDoor,number, forbiddenDirections)
                    } else {
                        currentRow-=1
                        map[currentRow][currentCol] = number
                    }
            else -> if(map[currentRow+1][currentCol]!=""){
                        forbiddenDirections.add(nextRoom)
                        generateNextRoom(map,currentRow,currentCol,entranceDoor,number, forbiddenDirections)
                    } else {
                        currentRow+=1
                        map[currentRow][currentCol] = number
                    }
            }

        entranceDoor = getReverseDirection(nextRoom)

        return mapOf(
            "currentRow" to currentRow,
            "currentCol" to currentCol,
            "entranceDoor" to entranceDoor,
            "nextRoom" to nextRoom
        )
    }

    fun getReverseDirection(direction : String) : String{
        return when(direction){
            "left"->"right"
            "top"->"bottom"
            "right"->"left"
            else->"top"
        }
    }
    fun characterStartPosition() : Pair<Float,Float>{
        val firstRoomCenter = rooms!![0].getRoomCenter()
        return Pair(
            firstRoomCenter.first,
            firstRoomCenter.second
        )
    }

    fun inOpenDoor(x : Float, y: Float) : Boolean{
        val globalPosition = getMapIndexFromPosition(x,y)
        val room = getRoomFromMapIndex(globalPosition.first, globalPosition.second)
        return room!!.getElement(x,y) in listOf<String>("U","V","W","X")
    }
    fun reload(){
        generateMap()
        tileMap = makeTileMap()
        tileArea = makeTileArea()
        if(!enemies.isEmpty()){
            enemies[0].game.reloadBackground()
        }
    }

    fun currentRoom() : Room{
        return rooms!![currentRoom]
    }

    fun previousRoom() : Room{
        return rooms!![currentRoom-1]
    }

    fun nextRoom() : Room{
        return rooms!![currentRoom+1]
    }








}