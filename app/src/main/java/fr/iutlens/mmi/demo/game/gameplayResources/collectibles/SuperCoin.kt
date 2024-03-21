package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible

class SuperCoin(game: Game) : Collectible(game, spriteIndex = 6, sound = R.raw.coin, collectEffect = {
    game.coins.value+=5
}) {
}