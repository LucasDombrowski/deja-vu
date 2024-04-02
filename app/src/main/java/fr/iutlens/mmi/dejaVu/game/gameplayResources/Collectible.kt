package fr.iutlens.mmi.dejaVu.game.gameplayResources

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite

open class Collectible (val game: Game, spriteIndex: Int, val sound : Int, val collectEffect : ()->Unit){

    val sprite = BasicSprite(R.drawable.collectibles,0f,0f,spriteIndex)
    fun setup(x:Float, y:Float){
        sprite.x = x
        sprite.y = y
        game.spriteList.add(sprite)
        game.collectibleList.add(this)
    }

    fun destroy(){
        game.deleteSprite(sprite)
        game.collectibleList.remove(this)
    }
}