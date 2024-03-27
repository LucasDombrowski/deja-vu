package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.R
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart
import fr.iutlens.mmi.demo.utils.Music


open class Item(val image: Int, val name : String, val description : String, val major : Boolean = true, val effects : (game : Game)->Unit) {
    fun get(game: Game){
        val item = this
        item.effects(game)
        if(major) {
            val soundVolume = 0.25f
            Music.playSound(R.raw.new_item, leftVolume = soundVolume, rightVolume = soundVolume)
            game.controllableCharacter!!.items.add(item)
            game.item["description"] = item.description
            game.item["image"] = item.image
            game.item["name"] = item.name
            game.item["show"] = true
        }
    }
}