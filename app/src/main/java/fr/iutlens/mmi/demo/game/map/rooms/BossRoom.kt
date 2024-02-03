package fr.iutlens.mmi.demo.game.map.rooms

import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room

class BossRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit) {
    override fun create(): String {
        val lastRoom =when (enter) {
            "top" ->  """
                            0122222O3333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            I!!!!!!!!!!!!!D
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888999999AB
                            """.trimIndent()
            "bottom" -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            I!!!!!!!!!!!!!D
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888P99999AB
                            """.trimIndent()
            "left" -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            Q!!!!!!!!!!!!!D
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888999999AB
                            """.trimIndent()
            "right" -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            I!!!!!!!!!!!!!R
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888999999AB
                            """.trimIndent()
            else -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            I!!!!!!!!!!!!!D
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888999999AB
                            """.trimIndent()
        }



        return lastRoom
    }

    override fun copy(): Room {
        return BossRoom(map, enter, exit)
    }
}
