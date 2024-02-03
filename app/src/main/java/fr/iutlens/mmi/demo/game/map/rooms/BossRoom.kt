package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room

class BossRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit) {
    override fun create(): String {
        val firstRoom = """
        012222233333345
        C!!!!!!!!!!!!!F
        I!!!!!!!!!!!!!L
        I!!!!!!!!!!!!!D
        E!!!!!!!!!!!!!D
        K!!!!!!!!!!!!!J
        6788888P99999AB
        """.trimIndent()

        return firstRoom
    }

    override fun copy(): Room {
        return BossRoom(map, enter, exit)
    }
}
