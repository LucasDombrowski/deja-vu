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

}