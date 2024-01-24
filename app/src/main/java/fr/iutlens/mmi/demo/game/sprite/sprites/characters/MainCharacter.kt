package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character

class MainCharacter(x: Float, y:Float, game: Game) : Character(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 10f,
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    topLeftAnimationSequence = listOf(3,4,5),
    topRightAnimationSequence = listOf(6,7,8),
    bottomLeftAnimationSequence = listOf(3,4,5),
    bottomRightAnimationSequence = listOf(6,7,8)
)