package fr.iutlens.mmi.demo.game.levels

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Chest
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaBoots
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaScarf
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaShuriken
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.Buddy
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.RangeNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.TeleportNinja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses.NinjaBoss
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class Ninja : Game(
    map = Map(
        roomInterval = 5..7,
        drawable = R.drawable.decor,
        treasureRooms = 1
    )
) {
    init {
        map.enemies = listOf(
            CloseNinja(0f,0f,this),
            RangeNinja(0f,0f,this),
            TeleportNinja(0f,0f,this)
        )
        items += mutableListOf<Item>(
            NinjaScarf(),
            NinjaShuriken(),
            NinjaBoots()
        )
        map.boss = NinjaBoss(0f,0f,this)
        controllableCharacter!!.changeProjectileSkin(
            4
        ){
            projectile -> setInterval(0,33){
                if(projectile.sprite.rotate>=360f){
                    projectile.sprite.rotate = 0f
                } else {
                    projectile.sprite.rotate+=36f
                }
            }
        }
    }

    override fun copy() : Ninja{
        return Ninja()
    }
}