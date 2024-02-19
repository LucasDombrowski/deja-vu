package fr.iutlens.mmi.demo.game.map

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.rooms.BasicRoom
import fr.iutlens.mmi.demo.game.map.rooms.BossRoom
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
    var mapPath : List<List<String>> ?= null
    var rooms : List<Room> ?= null
    var roomSequence : List<String>? = null
    var currentRoom = 0

    fun makeTileMap() : ArrayTileMap{
        if(mapString==null) {
            generateMap()
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
    fun generateMapPath() : List<List<String>>{
        var entranceDoor : String ?= null
        var n = 1

        val sequence = mutableListOf<String>()
        val map = mutableListOf<MutableList<String>>(
        )
        repeat((roomNumber*2)+1){
            val mapRow = mutableListOf<String>()
            repeat((roomNumber*2)+1){
                mapRow.add("")
            }
            map.add(mapRow)
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
        }
        map.map {
            it.toList()
        }
        roomSequence = sequence.toList()
        return map.toList()
    }

    fun generateMap(){
        if(mapPath==null){
            mapPath = generateMapPath()
        }
        Log.i("monTest", "$mapPath")
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
                            row.add(rooms!![it.toInt()-1].composition)
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

    fun generateRooms(){
        val currentMap = this
        var room : Room = BasicRoom(currentMap)
        val lastRoom = roomNumber+1
        val rowsToAdd = room.row
        val colsToAdd = room.col
        var currentRow = 0
        var currentCol = 0
        val roomList : MutableMap<String,Room> = mutableMapOf()
        val randomTreasureRooms = mutableListOf<String>()
        repeat(treasureRooms){
            var number = (2..roomNumber).random().toString()
            while(number in randomTreasureRooms){
                number = (2..roomNumber).random().toString()
            }
            randomTreasureRooms.add(number)
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
                                 else->BasicRoom(currentMap)
                             }
                            val newRoom = room
                            newRoom.topLeftCorner = Pair(currentRow, currentCol)
                            newRoom.bottomRightCorner = Pair(currentRow+rowsToAdd, currentCol+colsToAdd)
                            roomList[it] = newRoom

                        }
                        currentCol+=colsToAdd
                    }
                }
                currentRow+=rowsToAdd
            }
        }
        rooms = roomList.toSortedMap().values.toList()

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