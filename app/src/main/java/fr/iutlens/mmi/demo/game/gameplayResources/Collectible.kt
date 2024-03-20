package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite

open class Collectible (val game: Game, spriteIndex: Int, val collectEffect : ()->Unit){

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