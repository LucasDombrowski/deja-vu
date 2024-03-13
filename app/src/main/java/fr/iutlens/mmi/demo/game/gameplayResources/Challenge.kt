package fr.iutlens.mmi.demo.game.gameplayResources

import fr.iutlens.mmi.demo.game.Game

open class Challenge(val name: String, val effect : (Game)->Unit, val reverseEffect : (Game)->Unit) {
}