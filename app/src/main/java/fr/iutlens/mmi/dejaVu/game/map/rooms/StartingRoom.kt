package fr.iutlens.mmi.dejaVu.game.map.rooms

import fr.iutlens.mmi.dejaVu.game.map.Map
import fr.iutlens.mmi.dejaVu.game.map.Room

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