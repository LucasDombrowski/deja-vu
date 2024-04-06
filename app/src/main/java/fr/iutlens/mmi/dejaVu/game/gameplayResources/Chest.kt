package fr.iutlens.mmi.dejaVu.game.gameplayResources

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.utils.Music
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Chest(val itemList: List<Item>) {

    val sprite : BasicSprite = BasicSprite(R.drawable.treasure_chest,0f,0f)

    fun open(game: Game){
        game.controllableCharacter!!.temporaryMovingInteraction = {
            x, y ->
        }
        game.solidSpriteList.add(sprite)
        val tile = game.map.getMapIndexFromPosition(game.controllableCharacter!!.sprite.x,game.controllableCharacter!!.sprite.y)
        val nextTile = when(game.controllableCharacter!!.solidSpriteInTile(tile.first,tile.second)){
            true->when(game.controllableCharacter!!.currentDirection){
                "right"->Pair(tile.first,tile.second-1)
                "left"->Pair(tile.first,tile.second+1)
                "top"->Pair(tile.first+1,tile.second)
                "bottom"->Pair(tile.first-1,tile.second)
                else->Pair(tile.first,tile.second)
            }
            else->tile
        }
        val floatTile = game.map.getPositionFromMapIndex(nextTile.first,nextTile.second)
        game.controllableCharacter!!.changePos(
            floatTile.first + game.map.tileArea.w/2,
            floatTile.second + game.map.tileArea.h/2
        )
        game.controllableCharacter!!.stun()
        val soundVolume = 0.075f
        Music.playSound(R.raw.open_chest, leftVolume = soundVolume, rightVolume = soundVolume)
        game.pause = true
        openSprite()
        val item = itemList.random()
        val itemDelay = 500L
        GlobalScope.launch {
            delay(itemDelay)
            game.controllableCharacter!!.restart()
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