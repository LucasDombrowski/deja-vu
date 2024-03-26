package fr.iutlens.mmi.demo.game.gameplayResources.collectibles

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Collectible
import fr.iutlens.mmi.demo.game.gameplayResources.Heart
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.demo.game.sprite.sprites.BigBook

class HeartContainer(game: Game): Collectible(game, spriteIndex = 1, sound = R.raw.grab_red_heart, collectEffect = {
    OneHeart().get(game)
    game.item["onPick"] = {
        val bigBook = BigBook(game, game.map.currentRoom().getRoomCenter().first, game.map.currentRoom().getRoomCenter().second)
        bigBook.setup()
    }
})