package fr.iutlens.mmi.demo.game.gameplayResources

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class Chest(val itemList: List<Item>) {

    val sprite : BasicSprite = BasicSprite(R.drawable.treasure_chest,0f,0f)

    var openable : Job = GlobalScope.launch {
        return@launch
    }
    fun open(game: Game){
        game.pause = true
        openable.cancel()
        openSprite()
        val item = itemList.random()
        item.effects(game)
        game.controllableCharacter!!.items.add(item)
        game.item["description"] = item.description
        game.item["image"] = item.image
        game.item["name"] = item.name
        game.item["show"] = true
        openable = GlobalScope.launch {
            return@launch
        }
    }

    fun openSprite(){
        sprite.ndx = 1
    }

    fun closeSprite(){
        sprite.ndx = 0
    }

    fun setup(x: Float, y:Float, game: Game){
        sprite.x = x
        sprite.y = y
        game.addSprite(sprite)
        openable = setInterval(0,33){
            if(game.controllableCharacter!!.inBoundingBox(sprite.x,sprite.y)){
                open(game);
            }
        }
    }
}