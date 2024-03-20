package fr.iutlens.mmi.demo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import fr.iutlens.mmi.demo.boot.changeLevel
import fr.iutlens.mmi.demo.boot.startFirstLevel
import fr.iutlens.mmi.demo.components.Level
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.loadSound

import fr.iutlens.mmi.demo.utils.loadSpritesheet

var currentContext : Context ?= null

fun getCurrentActivityContext() : Context{
    return currentContext!!
}
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSpritesheet(R.drawable.ninja_level,12,6)
        loadSpritesheet(R.drawable.isaac, 9, 4)
        loadSpritesheet(R.drawable.big_isaac, 3, 4)
        loadSpritesheet(R.drawable.projectiles,6,1)
        loadSpritesheet(R.drawable.transparent, 1,1)
        loadSpritesheet(R.drawable.target_indicator, 1,1)
        loadSpritesheet(R.drawable.path_indicator, 1,1)
        loadSpritesheet(R.drawable.treasure_chest,2,1)
        loadSpritesheet(R.drawable.chrono,6,4)
        loadSpritesheet(R.drawable.smoke_animation,3,2)
        loadSpritesheet(R.drawable.heart_container,1,1)
        loadSpritesheet(R.drawable.coin_drop,1,1)

        loadSound(R.raw.message)

        setContent {
            currentContext = LocalContext.current
            var game by remember {
                mutableStateOf(startFirstLevel())
            }
            MyApplicationTheme {
                Level(game = game, onEnd = {
                    game = changeLevel(game)
                }, onRestart = {
                    game = startFirstLevel()
                })
                Music(id = R.raw.jungle)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mute = true
    }

}


