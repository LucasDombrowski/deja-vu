package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room

class BossRoom(map : Map, enter: String ?= null) :  BasicRoom(enter=enter, exit = null, map = map) {
    override fun copy(): Room {
        return BossRoom(map, enter)
    }
}
