package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room

open class BasicRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit, enemies = 2..3) {

}