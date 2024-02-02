package fr.iutlens.mmi.demo.game.levels

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.RangeNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.TeleportNinja
import kotlin.reflect.KClass

class Ninja : Game(
    map = Map(
        roomInterval = 5..7,
        drawable = R.drawable.decor
    )
) {

}