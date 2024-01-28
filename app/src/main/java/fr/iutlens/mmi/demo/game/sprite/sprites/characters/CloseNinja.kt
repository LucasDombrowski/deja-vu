package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.utils.setInterval
import kotlin.math.abs

class CloseNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 10f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
){
    override var action = setInterval(0,100){
        if(!target!!.inBoundingBox(sprite.x,sprite.y)) {
            moveTo(target!!.sprite.x, target!!.sprite.y)
        } else {
            target!!.healthDown(0.33f, 20f, currentDirection)
        }
    }
}