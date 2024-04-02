package fr.iutlens.mmi.dejaVu.game.gameplayResources

import fr.iutlens.mmi.dejaVu.R
import fr.iutlens.mmi.dejaVu.game.Game
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
            game.item["show"] = true
        }
    }
}