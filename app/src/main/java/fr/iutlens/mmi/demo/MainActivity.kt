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
import fr.iutlens.mmi.demo.game.screens.MainMenu
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.Music.playSound
import fr.iutlens.mmi.demo.utils.loadSound

import fr.iutlens.mmi.demo.utils.loadSpritesheet

var currentContext : Context ?= null

fun getCurrentActivityContext() : Context{
    return currentContext!!
}
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSpritesheet(R.drawable.ninja_level,16,6)
        loadSpritesheet(R.drawable.projectiles,6,1)
        loadSpritesheet(R.drawable.transparent, 1,1)
        loadSpritesheet(R.drawable.target_indicator, 1,1)
        loadSpritesheet(R.drawable.path_indicator, 1,1)
        loadSpritesheet(R.drawable.treasure_chest,2,1)
        loadSpritesheet(R.drawable.chrono,6,4)
        loadSpritesheet(R.drawable.smoke_animation,3,2)
        loadSpritesheet(R.drawable.collectibles,4,2)
        loadSpritesheet(R.drawable.isaac, 9, 4)
        loadSpritesheet(R.drawable.first_boss, 5, 8)
        loadSpritesheet(R.drawable.portal_book,1,1)
        loadSpritesheet(R.drawable.shopkeeper,1,1)

        loadSound(R.raw.text_sound_effect)
        loadSound(R.raw.coin)
        loadSound(R.raw.heart)
        loadSound(R.raw.victory)

        setContent {
            currentContext = LocalContext.current

            var game by remember {
                mutableStateOf(startFirstLevel())
            }

            var started by remember {
                mutableStateOf(false)
            }

            MyApplicationTheme {
                if(started){
                    Level(game = game, onEnd = {
                        game = changeLevel(game)
                    }, onRestart = {
                        game = startFirstLevel()
                    },
                        onLeave = {
                            started = false
                            game = startFirstLevel()
                        })
                } else {
                    MainMenu(
                        onStart = {
                            started = true
                        },
                        onLeave = {
                            finishAndRemoveTask()
                        }
                    )
                }
            }
        }


    }

    override fun onPause() {
        super.onPause()
        mute = true
    }


}


