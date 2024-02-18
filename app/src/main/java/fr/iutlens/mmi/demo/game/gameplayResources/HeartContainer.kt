package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite

class HeartContainer {

    val spriteImage = R.drawable.permanent_heart_4_4
    fun setup(x: Float, y: Float, game: Game){
        val heart = BasicSprite(spriteImage, x, y)
        game.addSprite(heart)
        game.controllableCharacter!!.temporaryMovingInteraction = {
                x, y ->
                if(x in heart.boundingBox.left..heart.boundingBox.right && y in heart.boundingBox.top..heart.boundingBox.bottom){
                    game.controllableCharacter!!.hearts.add(Heart(true,1f))
                    for(characterHeart in game.controllableCharacter!!.hearts){
                        if(characterHeart.permanent){
                            characterHeart.filled = 1f
                        }
                    }
                    game.controllableCharacter!!.refreshHeathBar()
                    game.deleteSprite(heart)
                    game.controllableCharacter!!.temporaryMovingInteraction = {
                        x, y ->
                    }
                }
        }
    }
}