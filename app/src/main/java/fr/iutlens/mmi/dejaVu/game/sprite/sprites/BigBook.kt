package fr.iutlens.mmi.dejaVu.game.sprite.sprites

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round

class BigBook(val game : Game, x : Float, y: Float) : BasicSprite(
    R.drawable.portal_book,
    x,
    y,
    0
) {
    fun setup(){
        removeControls()
        scaleX=0f
        scaleY=0f
        game.addSprite(this)
        appear()
    }

    fun appear(){
        val scaleStepDelay = 33L
        val scaleStep = 0.1f
        val actionDelay = 500L
        scaleX = 0f
        scaleY = 0f
        game.invalidate()
        GlobalScope.launch {
            while (scaleX<1f && scaleY<1f){
                scaleX+=scaleStep
                scaleY+=scaleStep
                game.invalidate()
                delay(scaleStepDelay)
            }
            delay(actionDelay)
            action()
        }
    }

    fun disappear(){
        val scaleStepDelay = 33L
        val scaleStep = 0.1f
        val endDelay = 500L
        scaleX = 1f
        scaleY = 1f
        game.invalidate()
        GlobalScope.launch {
            while (scaleX>0f && scaleY>0f){
                scaleX-=scaleStep
                scaleY-=scaleStep
                game.invalidate()
                delay(scaleStepDelay)
            }
            delay(endDelay)
            game.onEnd()
        }
    }

    fun removeControls(){
        game.onDragMove = {}
        game.onDragStart = {}
        game.onDragEnd = {}
        game.onTap = {}
        game.controllableCharacter!!.characterAnimation.cancel()
    }

    fun action(){
        val positionCheckDelay = 33L
        val steps = getMoveSteps()
        val vanishDelay = 500L
        GlobalScope.launch {
            while (!inBookBoundingBox()){
                game.controllableCharacter!!.sprite.x+=steps.first
                game.controllableCharacter!!.sprite.y+=steps.second
                game.invalidate()
                delay(positionCheckDelay)
            }
            game.controllableCharacter!!.moveTo(x,y)
            delay(vanishDelay)
            vanish()
        }
    }

    fun getMoveSteps() : Pair<Float,Float>{
        val aimedX = x
        val aimedY = y
        val steps = moveStep()
        val xStep = when{
            aimedX<game.controllableCharacter!!.sprite.x->-steps[0]
            else->steps[0]
        }
        val yStep = when{
            aimedY<game.controllableCharacter!!.sprite.y->-steps[1]
            else->steps[1]
        }
        return Pair(xStep,yStep)
    }

    fun moveStep() : List<Float>{
        val moveSpeed = 0.3f*((game.map.tileArea.w + game.map.tileArea.h)/2)
        val aimedX = x
        val aimedY = y
        val vectorX = abs( round(aimedX) - round(game.controllableCharacter!!.sprite.x))
        val vectorY = abs(round(aimedY) - round(game.controllableCharacter!!.sprite.y))
        return if (vectorX == 0f && vectorY == 0f) {
            listOf(0f, 0f)
        } else if (vectorX == 0f) {
            listOf(0f, moveSpeed)
        } else if(vectorY==0f){
            listOf(moveSpeed,0f)
        } else if(vectorX==vectorY){
            listOf(moveSpeed,moveSpeed)
        } else if(vectorX>vectorY){
            listOf(moveSpeed,moveSpeed/(vectorX/vectorY))
        } else {
            listOf(moveSpeed/(vectorY/vectorX),moveSpeed)
        }
    }

    fun inBookBoundingBox() : Boolean{
        return game.controllableCharacter!!.sprite.x > boundingBox.left+(boundingBox.right-boundingBox.left)/4 && game.controllableCharacter!!.sprite.x < boundingBox.right-(boundingBox.right-boundingBox.left)/4 && game.controllableCharacter!!.sprite.y > boundingBox.top+(boundingBox.bottom-boundingBox.top)/4 && game.controllableCharacter!!.sprite.y < boundingBox.bottom-(boundingBox.bottom-boundingBox.top)/4
    }

    fun vanish(){
        val vanishStepTime = 33L
        val vanishStep = 0.25f
        var invisibleValue = 1f
        GlobalScope.launch {
            while (invisibleValue>=0f){
                game.controllableCharacter!!.sprite.setTransparencyLevel(invisibleValue)
                game.invalidate()
                invisibleValue-=vanishStep
                delay(vanishStepTime)
            }
            disappear()
        }
    }

}