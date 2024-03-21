package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart

class HeartContainer(game: Game): Collectible(game, spriteIndex = 1, sound = R.raw.heart, collectEffect = {
    OneHeart().get(game)
    game.item["onPick"] = {
        game.onEnd()
    }
})