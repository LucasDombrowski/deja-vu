package fr.iutlens.mmi.dejaVu.game.gameplayResources

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.boot.checkedTutorials
import fr.iutlens.mmi.dejaVu.boot.commitBooleanSharedPreferences
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.NinjaScarf
import fr.iutlens.mmi.dejaVu.game.gameplayResources.items.Wallet
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialCoins
import fr.iutlens.mmi.dejaVu.game.screens.cinematic.cinematics.TutorialGoldHeart
import fr.iutlens.mmi.dejaVu.utils.Music


open class Item(val image: Int, val name : String, val description : String, val major : Boolean = true, val effects : (game : Game)->Unit) {

    fun get(game: Game, sound : Boolean = true){
        val item = this
        item.effects(game)
        if(major) {
            if(sound) {
                val soundVolume = 0.25f
                Music.playSound(R.raw.new_item, leftVolume = soundVolume, rightVolume = soundVolume)
            }
            game.controllableCharacter!!.items.add(item)
            game.item["description"] = item.description
            game.item["image"] = item.image
            game.item["name"] = item.name
            if(isGoldHeartItem() && game.firstTime && !checkedTutorials["goldHeart"]!!){
                game.item["onPick"] = {
                    game.athOverride["hearts"] = true
                    game.cinematic.value = Pair(
                        TutorialGoldHeart(game){
                            game.athOverride["hearts"] = false
                            commitBooleanSharedPreferences("goldHeartTutorial",true)
                        },
                        true
                    )
                }
            } else if(isCoinItem() && game.firstTime && !checkedTutorials["coins"]!!){
                game.athOverride["coins"] = true
                game.cinematic.value = Pair(
                    TutorialCoins(game){
                        game.athOverride["coins"] = false
                        commitBooleanSharedPreferences("coinsTutorial",true)
                    },
                    true
                )
            }
            game.item["show"] = true
        }
    }

    fun isGoldHeartItem() : Boolean{
        val goldHeartItems = listOf<Item>(NinjaScarf())
        goldHeartItems.forEach {
            if(this.name == it.name){
                return true
            }
        }
        return false
    }

    fun isCoinItem() : Boolean {
        val coinItems = listOf(Wallet())
        coinItems.forEach {
            if(this.name == it.name){
                return true
            }
        }
        return false
    }
}