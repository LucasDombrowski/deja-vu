package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.dejaVu.game.sprite.sprites.BigBook

class HeartContainer(game: Game): Collectible(game, spriteIndex = 1, sound = R.raw.grab_heart_container, collectEffect = {
    OneHeart().get(game, false)
    game.item["onPick"] = {
        val bigBook = BigBook(game, game.map.currentRoom().getRoomCenter().first, game.map.currentRoom().getRoomCenter().second)
        bigBook.setup()
    }
})