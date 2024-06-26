package fr.iutlens.mmi.dejaVu.game.sprite.sprites

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Heart
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.Cinematic
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.CinematicPart
import fr.iutlens.mmi.dejaVu.game.sprite.BasicSprite
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class Boss(
    val endCinematicParts : List<CinematicPart>,
    val image : Int,
    sprite: BasicSprite,
    game: Game,
    speed:Float,
    hearts: MutableList<Heart>,
    basicAnimationSequence: List<Int>,
    leftAnimationSequence: List<Int> = basicAnimationSequence,
    topAnimationSequence: List<Int> = basicAnimationSequence,
    rightAnimationSequence: List<Int> = basicAnimationSequence,
    bottomAnimationSequence : List<Int> = basicAnimationSequence,
    target : Character? = null,
    fireRate : Long = 0,
    action : Job = GlobalScope.launch {
        return@launch
    }
) : Enemy(
    sprite,
    game,
    speed,
    hearts,
    basicAnimationSequence,
    leftAnimationSequence,
    topAnimationSequence,
    rightAnimationSequence,
    bottomAnimationSequence,
    target,
    fireRate,
    action = action){
    override fun hit(damages: Float, knockback: Float, direction: String){
        healthDown(damages, 0f, direction)
        GlobalScope.launch {
            sprite.semiWhiteColor()
            delay(100)
            sprite.permanentColor()
        }
        refreshHeathBar()

    }

    fun refreshHeathBar(){
        val newHearts : MutableList<Heart> = mutableListOf()
        for(heart in hearts){
            newHearts.add(heart.copy())
        }
        game.ath["boss"] = newHearts
    }

    open fun startCinematic() : Cinematic{
        return Cinematic(game = game)
    }

    open fun endCinematic() : Cinematic{
        return Cinematic(game = game)
    }



}