package fr.iutlens.mmi.demo.game.sprite.sprites.characters

import android.util.Log
import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.sprites.Enemy
import fr.iutlens.mmi.demo.utils.setInterval
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

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
        action.cancel()
        action = setInterval(0,100){
            if(!target!!.inBoundingBox(sprite.x,sprite.y)) {
                moveTo(target!!.sprite.x, target!!.sprite.y)
            } else {
                target!!.healthDown(damages, knockback, currentDirection)
            }
        }
    }

    fun followShortestPath(damages: Float, knockback: Float){
        action.cancel()
        val path = getShortestPath(target!!.sprite.x, target!!.sprite.y)
        if(path.isNotEmpty()) {
            val pathPositions = mutableListOf<Pair<Float, Float>>()
            with(path.iterator()) {
                forEach {
                    pathPositions.add(
                        Pair(
                            game.map.getPositionFromMapIndex(it.first, it.second).first,
                            game.map.getPositionFromMapIndex(it.first, it.second).second
                        )
                    )
                }
            }
            var pathIndex = 0
            action = setInterval(0, 100) {
                if (round(sprite.x) != pathPositions[pathIndex].first || round(sprite.y) != pathPositions[pathIndex].second) {
                    moveTo(pathPositions[pathIndex].first, pathPositions[pathIndex].second)
                } else if (pathIndex == path.size - 1) {
                    attackPlayer(damages, knockback)
                } else {
                    pathIndex++
                }
            }
        } else {
            attackPlayer(0.5f, 0.2f)
        }

    }
}