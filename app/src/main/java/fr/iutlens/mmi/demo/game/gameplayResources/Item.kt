package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.items.OneHeart


open class Item(val image: Int, val name : String, val description : String, val effects : (game : Game)->Unit) {
    fun get(game: Game){
        val item = this
        item.effects(game)
        game.controllableCharacter!!.items.add(item)
        game.item["description"] = item.description
        game.item["image"] = item.image
        game.item["name"] = item.name
        game.item["show"] = true
    }
}