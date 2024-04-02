package fr.iutlens.mmi.dejaVu.game.levels

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Item
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.NinjaBoots
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.NinjaScarf
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.NinjaShuriken
import fr.iutlens.mmi.dejaVu.game.map.Map
import fr.iutlens.mmi.dejaVu.game.screens.screenEffects.Fog
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.RangeNinja
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.TeleportNinja
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.bosses.NinjaBoss
import fr.iutlens.mmi.dejaVu.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Ninja : Game(
    map = Map(
        roomInterval = 6..8,
        drawable = R.drawable.ninja_level,
        treasureRooms = 1
    ),
    backgroundMusic = R.raw.first_level,
    screenEffect = {
        Fog(animate = it)
    }
) {
    init {
        map.enemies = listOf(
            CloseNinja(0f, 0f, this),
            RangeNinja(0f, 0f, this),
            TeleportNinja(0f, 0f, this)
        )
        items += mutableListOf<Item>(
            NinjaScarf(),
            NinjaShuriken(),
            NinjaBoots()
        )
        map.boss = NinjaBoss(0f, 0f, this)
        controllableCharacter!!.changeProjectileSkin(
            4
        ) { projectile ->
            setInterval(0, 33) {
                if (projectile.sprite.rotate >= 360f) {
                    projectile.sprite.rotate = 0f
                } else {
                    projectile.sprite.rotate += 36f
                }
            }
        }
    }

    override fun copy() : Ninja{
        return Ninja()
    }
}