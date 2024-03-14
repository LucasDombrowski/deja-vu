package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import java.lang.StringBuilder

class StartingRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit, enemies = 0..0) {

    override var composition = create(false).trimIndent()

    override fun copy(): Room {
        return StartingRoom(map, enter, exit)
    }

    override fun refresh(){
        composition = create(false).trimIndent()
        toList()
    }
}