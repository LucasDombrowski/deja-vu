package fr.iutlens.mmi.demo.game.map.rooms

import android.util.Log
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room

open class StartingRoom(map : Map, enter: String ?= null, exit: String? = null) : Room(row = 7, col = 15, map = map, enter = enter, exit = exit, enemies = 0..0) {
    override fun create(): String {
        val firstRoom =when (exit) {
            "top" ->  """
                            0122222U3333345
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
                            6788888V99999AB
                            """.trimIndent()
            "left" -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            W!!!!!!!!!!!!!D
                            E!!!!!!!!!!!!!D
                            K!!!!!!!!!!!!!J
                            6788888999999AB
                            """.trimIndent()
            "right" -> """
                            012222233333345
                            C!!!!!!!!!!!!!F
                            I!!!!!!!!!!!!!L
                            I!!!!!!!!!!!!!X
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


        return firstRoom
    }

    override fun copy(): Room {
        return StartingRoom(map, enter, exit)
    }
}