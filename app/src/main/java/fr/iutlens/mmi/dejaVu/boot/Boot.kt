package fr.iutlens.mmi.dejaVu.boot

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.levels.Ninja
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.MainCharacter

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
    level.coins = game.coins
    return level
}

fun startFirstLevel(firstTime : Boolean = true) : Game{
    val level = levels.random().copy()
    level.firstTime = firstTime
    saveMainCharacter(level)
    return level
}