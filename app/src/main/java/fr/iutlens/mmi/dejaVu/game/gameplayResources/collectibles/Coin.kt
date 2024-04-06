package fr.iutlens.mmi.dejaVu.game.gameplayResources.collectibles

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.Collectible
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialCoins
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialGoldHeart

class Coin(game: Game) : Collectible(game, spriteIndex = 0, sound = R.raw.grab_gold_coin, collectEffect = {
    game.coins.value++
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