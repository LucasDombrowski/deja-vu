package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.game.Game


open class Item(val image: Int, val name : String, val description : String, val effects : (game : Game)->Unit) {
}