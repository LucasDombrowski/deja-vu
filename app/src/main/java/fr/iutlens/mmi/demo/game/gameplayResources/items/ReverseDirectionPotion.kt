package fr.iutlens.mmi.demo.game.gameplayResources.items

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.gameplayResources.Item
import kotlin.math.abs

class ReverseDirectionPotion() : Item(
    image = R.drawable.potion,
    name = "Potion inversion contrôle",
    description = "blablablabla",
    effects = {
        game ->  game.controllableCharacter!!.movingBehavior = {
            x, y ->
            val xDifference : Float = abs(x - game.controllableCharacter!!.sprite.x)
            val yDifference : Float = abs(y - game.controllableCharacter!!.sprite.y)
            val xVal = when{
                x<game.controllableCharacter!!.sprite.x->xDifference
                x>game.controllableCharacter!!.sprite.x->-xDifference
                else->0f
            }
            val yVal = when{
                y<game.controllableCharacter!!.sprite.y->yDifference
                y>game.controllableCharacter!!.sprite.y->-yDifference
                else->0f
            }
            game.controllableCharacter!!.moveTo(game.controllableCharacter!!.sprite.x + xVal, game.controllableCharacter!!.sprite.y + yVal)
    }
    }
) {
}