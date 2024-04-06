package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialCoins
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialGoldHeart

class SuperCoin(game: Game) : Collectible(game, spriteIndex = 6, sound = R.raw.grab_silver_coin, collectEffect = {
    game.coins.value+=5
    if(game.firstTime && !checkedTutorials["coins"]!!){
        game.athOverride["coins"] = true
        game.cinematic.value = Pair(
            TutorialCoins(game){
                game.athOverride["coins"] = false
                commitBooleanSharedPreferences("coinsTutorial",true)
            },
            true
        )
    }
}) {
}