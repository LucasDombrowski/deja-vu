package fr.iutlens.mmi.demo.boot

import android.util.Log
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.levels.Ninja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.MainCharacter

var mainCharacter : MainCharacter ?= null
val levels : List<Game> = listOf(Ninja())

fun saveMainCharacter(game: Game){
    mainCharacter = game.controllableCharacter
}

fun randomLevel() : Game{
    val level = levels.random().copy()
    level.controllableCharacter!!.items = mainCharacter!!.items
    for(item in level.controllableCharacter!!.items){
        item.effects(level)
    }
    level.controllableCharacter!!.speed = mainCharacter!!.speed
    level.controllableCharacter!!.hearts = mainCharacter!!.hearts
    level.controllableCharacter!!.refreshHeathBar()
    level.controllableCharacter!!.fireRate = mainCharacter!!.fireRate
    level.controllableCharacter!!.fitToFireRate()
    level.controllableCharacter!!.projectile = mainCharacter!!.projectile.copy()
    return level
}

fun changeLevel(game: Game) : Game{
    saveMainCharacter(game)
    val level = randomLevel()
    level.onEnd = game.onEnd
    return level
}

fun startFirstLevel() : Game{
    val level = levels.random().copy()
    saveMainCharacter(level)
    return level
}