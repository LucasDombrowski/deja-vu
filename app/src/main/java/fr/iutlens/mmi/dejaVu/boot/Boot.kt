package fr.iutlens.mmi.dejaVu.boot

import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.levels.Ninja
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.characters.MainCharacter
import fr.iutlens.mmi.dejaVu.getCurrentSharedPreferences

var mainCharacter : MainCharacter ?= null
val levels : List<Game> = listOf(Ninja())


var checkedTutorials  : Map<String,Boolean> = mapOf(
)

fun resetCheckedTutorials(){
    checkedTutorials  = mapOf(
        "moving" to getCurrentSharedPreferences().getBoolean("movingTutorial",false),
        "fighting" to getCurrentSharedPreferences().getBoolean("fightingTutorial",false),
        "openRoom" to getCurrentSharedPreferences().getBoolean("openRoomTutorial",false),
        "chest" to getCurrentSharedPreferences().getBoolean("chestTutorial",false),
        "shop" to getCurrentSharedPreferences().getBoolean("shopTutorial",false),
        "goldHeart" to getCurrentSharedPreferences().getBoolean("goldHeartTutorial",false),
        "coins" to getCurrentSharedPreferences().getBoolean("coinsTutorial",false)
    )
}

fun commitBooleanSharedPreferences(tag : String, value : Boolean){
    getCurrentSharedPreferences().edit().putBoolean(tag,value).commit()
    resetCheckedTutorials()
}

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