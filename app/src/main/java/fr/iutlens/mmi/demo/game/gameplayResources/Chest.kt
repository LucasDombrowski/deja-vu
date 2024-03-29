package fr.iutlens.mmi.demo.game.gameplayResources

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Chest(val itemList: List<Item>) {

    val sprite : BasicSprite = BasicSprite(R.drawable.treasure_chest,0f,0f)

    fun open(game: Game){
        game.controllableCharacter!!.temporaryMovingInteraction = {
            x, y ->
        }
        game.solidSpriteList.add(sprite)
        val soundVolume = 0.075f
        Music.playSound(R.raw.open_chest, leftVolume = soundVolume, rightVolume = soundVolume)
        game.pause = true
        openSprite()
        val item = itemList.random()
        val itemDelay = 500L
        GlobalScope.launch {
            delay(itemDelay)
            item.get(game)
        }
    }

    fun openSprite(){
        sprite.ndx = 1
    }

    fun closeSprite(){
        sprite.ndx = 0
    }

    fun setCharacterInteraction(game: Game) : (x:Float, y:Float)->Unit{
        return {
                x, y ->
                if(x in sprite.boundingBox.left..sprite.boundingBox.right && y in sprite.boundingBox.top..sprite.boundingBox.bottom && (!game.firstTime || game.camera.chestTutorial)){
                    open(game)
                }
        }
    }

    fun setup(x: Float, y:Float, game: Game){
        sprite.x = x
        sprite.y = y
        game.addSprite(sprite)
        game.controllableCharacter!!.temporaryMovingInteraction = setCharacterInteraction(game)
    }
}