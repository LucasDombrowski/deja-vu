package fr.iutlens.mmi.dejaVu.game.gameplayResources

import fr.iutlens.mmi.dejaVu.game.Game

open class Challenge(val name: String, val effect : (Game)->Unit, val reverseEffect : (Game)->Unit) {
}