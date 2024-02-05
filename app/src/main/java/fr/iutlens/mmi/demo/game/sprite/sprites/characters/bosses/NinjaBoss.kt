package fr.iutlens.mmi.demo.game.sprite.sprites.characters.bosses

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Boss
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NinjaBoss(x: Float, y: Float, game: Game) : Boss(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(20),
    leftAnimationSequence = listOf(3,4,5),
    topAnimationSequence = listOf(9,10,11),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(6,7,8),
    target = game.controllableCharacter!!,
) {

    override fun copy() : NinjaBoss{
        return NinjaBoss(sprite.x,sprite.y, game)
    }

    override fun spawn(x: Float, y:Float){
        game.ath["boss"] = hearts
        game.addCharacter(this)
        changePos(x,y)
        action = GlobalScope.launch {
            return@launch
        }
    }
}