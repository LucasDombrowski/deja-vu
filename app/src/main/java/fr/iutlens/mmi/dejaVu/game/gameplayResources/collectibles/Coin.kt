package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible

class Coin(game: Game) : Collectible(game, spriteIndex = 0, sound = R.raw.grab_gold_coin, collectEffect = {
    game.coins.value++
}) {
}