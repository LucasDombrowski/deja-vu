package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainCharacter(x: Float, y:Float, game: Game) : Character(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 20f,
    damages = 1f,
    knockback = 10f,
    invulnerability = 750,
    hearts = setBasicHearts(3),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    rightAnimationSequence = listOf(6,7,8),
    bottomAnimationSequence = listOf(0,1,2)
    ){
fun refreshHeathBar(){
    val newHearts : MutableList<Heart> = mutableListOf()
    for(heart in hearts){
        newHearts.add(heart.copy())
    }
    game.ath["hearts"] = newHearts
}
}