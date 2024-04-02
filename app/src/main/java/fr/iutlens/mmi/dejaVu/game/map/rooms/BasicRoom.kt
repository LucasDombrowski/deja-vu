package fr.iutlens.mmi.dejaVu.game.map.rooms

import fr.iutlens.mmi.dejaVu.game.gameplayResources.Challenge
import fr.iutlens.mmi.dejaVu.game.map.Map
import fr.iutlens.mmi.dejaVu.game.map.Room

open class BasicRoom(map : Map, enter: String ?= null, exit: String? = null, challenge: Challenge) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit, enemies = 2..3, challenge = challenge) {

}