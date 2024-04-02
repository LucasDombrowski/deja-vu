package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible

class SuperCoin(game: Game) : Collectible(game, spriteIndex = 6, sound = R.raw.grab_silver_coin, collectEffect = {
    game.coins.value+=5
}) {
}