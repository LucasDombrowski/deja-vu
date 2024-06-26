package fr.iutlens.mmi.dejaVu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.dejaVu.boot.changeLevel
import fr.iutlens.mmi.dejaVu.boot.resetCheckedTutorials
import fr.iutlens.mmi.dejaVu.boot.startFirstLevel
import fr.iutlens.mmi.dejaVu.components.Level
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.screens.MainMenu
import fr.iutlens.mmi.dejaVu.utils.Music.mute
import fr.iutlens.mmi.dejaVu.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.dejaVu.utils.loadSound

import fr.iutlens.mmi.dejaVu.utils.loadSpritesheet

var currentContext : Context ?= null

var sharedPreferences : SharedPreferences ? = null

var currentGame : Game ? = null

fun getCurrentSharedPreferences() : SharedPreferences{
    return sharedPreferences!!
}
fun getCurrentActivityContext() : Context{
    return currentContext!!
}
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        actionBar?.hide()

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }



        enableEdgeToEdge()

        loadSpritesheet(R.drawable.ninja_level,16,6)
        loadSpritesheet(R.drawable.projectiles,6,1)
        loadSpritesheet(R.drawable.transparent, 1,1)
        loadSpritesheet(R.drawable.target_indicator, 1,1)
        loadSpritesheet(R.drawable.path_indicator, 1,1)
        loadSpritesheet(R.drawable.treasure_chest,2,1)
        loadSpritesheet(R.drawable.blaise,4,6)
        loadSpritesheet(R.drawable.smoke_animation,3,2)
        loadSpritesheet(R.drawable.collectibles,4,2)
        loadSpritesheet(R.drawable.isaac, 9, 4)
        loadSpritesheet(R.drawable.close_ninja,4,4)
        loadSpritesheet(R.drawable.range_ninja,4,4)
        loadSpritesheet(R.drawable.teleport_ninja,4,4)
        loadSpritesheet(R.drawable.first_boss, 5, 8)
        loadSpritesheet(R.drawable.portal_book,1,1)
        loadSpritesheet(R.drawable.shopkeeper,1,1)

        loadSound(R.raw.text_sound_effect)
        loadSound(R.raw.victory)
        loadSound(R.raw.grab_gold_coin)
        loadSound(R.raw.grab_red_heart)
        loadSound(R.raw.grab_silver_coin)
        loadSound(R.raw.grab_yellow_heart)
        loadSound(R.raw.hero_get_hit)
        loadSound(R.raw.hero_shoot)
        loadSound(R.raw.new_item)
        loadSound(R.raw.open_chest)
        loadSound(R.raw.open_menu)
        loadSound(R.raw.press_button)
        loadSound(R.raw.open_room)
        loadSound(R.raw.enemy_get_hit)
        loadSound(R.raw.purchase_item)
        loadSound(R.raw.hero_death)
        loadSound(R.raw.door)
        loadSound(R.raw.enemies_appear)
        loadSound(R.raw.enemy_death)
        loadSound(R.raw.ninja_shot)
        loadSound(R.raw.teleport)
        loadSound(R.raw.grab_heart_container)
        loadSound(R.raw.book_portal_appear)
        loadSound(R.raw.book_portal_disappear)
        loadSound(R.raw.ninja_boss_attack)
        loadSound(R.raw.shot_reflected)
        loadSound(R.raw.ninja_boss_dash)


        setContent {
            currentContext = LocalContext.current
            sharedPreferences = getSharedPreferences("Deja vu",0)
            resetCheckedTutorials()

            var game by remember {
                mutableStateOf(startFirstLevel())
            }

            var started by remember {
                mutableStateOf(false)
            }

            currentGame = game

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
        currentGame!!.pause = true
    }

    override fun onRestart() {
        super.onRestart()
        mute = false
        currentGame!!.pause = false
    }


}


