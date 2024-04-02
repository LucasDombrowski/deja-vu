package fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters

import android.util.Log
import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.Enemy
import fr.iutlens.mmi.dejaVu.utils.setInterval

class CloseNinja(x: Float, y:Float, game: Game) : Enemy(
    sprite = BasicSprite(R.drawable.isaac,x,y,1),
    game = game,
    basicAnimationSequence = listOf(1),
    speed = 0.05f,
    hearts = setBasicHearts(6),
    leftAnimationSequence = listOf(9,10,11),
    topAnimationSequence = listOf(27,28,29),
    bottomAnimationSequence = listOf(0,1,2),
    rightAnimationSequence = listOf(18,19,20),
    target = game.controllableCharacter!!,
){
    override fun spawn(x: Float, y:Float){
        game.addCharacter(this)
        changePos(x,y)
        attackPlayer(0.5f,0.2f)
    }

    override fun copy() : CloseNinja{
        val newCharacter = CloseNinja(sprite.x, sprite.y, game)
        newCharacter.sprite = newCharacter.sprite.copy()
        return newCharacter
    }

    fun attackPlayer(damages: Float, knockback: Float){
        disablePathFollowing()
        action.cancel()
        if(!game.ended) {
            action = setInterval(0, 100) {
                if (!target!!.inBoundingBox(sprite.x, sprite.y)) {
                    moveTo(target!!.sprite.x, target!!.sprite.y)
                    if (!isPathFree(target!!.sprite.x, target!!.sprite.y)) {
                        followPlayer()
                    }
                } else {
                    target!!.healthDown(damages, knockback, currentDirection)
                }
            }
        }
    }

    fun followPlayer(){
        action.cancel()
        setupPath(target!!.sprite.x, target!!.sprite.y)
        pathFollow = true
        action = setInterval(0,100){
            Log.i("pathFollow","true")
            if(isPathFree(target!!.sprite.x, target!!.sprite.y) || !pathFollow){
                attackPlayer(0.5f, 0.2f)
            }
        }
    }

}