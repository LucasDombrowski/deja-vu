package fr.iutlens.mmi.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.demo.components.Level
import fr.iutlens.mmi.demo.components.MainMenu
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.setBasicHearts
import fr.iutlens.mmi.demo.game.levels.Ninja
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.CloseNinja
import fr.iutlens.mmi.demo.utils.Music.mute
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.loadSound

import fr.iutlens.mmi.demo.utils.loadSpritesheet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSpritesheet(R.drawable.decor, 6, 7)
        loadSpritesheet(R.drawable.isaac, 3, 4)
        loadSpritesheet(R.drawable.tear,1,1)
        loadSpritesheet(R.drawable.transparent, 1,1)
        loadSpritesheet(R.drawable.arrow, 1,1)
        loadSpritesheet(R.drawable.treasure_chest,2,1)
        loadSpritesheet(R.drawable.chrono,6,4)

        loadSound(R.raw.message)


        setContent {
            MyApplicationTheme {
                var game : Game ? by remember{ mutableStateOf(null) }
                if(game == null){
                    MainMenu(){
                        game = Ninja()
                    }
                } else {
                    Level(game = game!!)
                }
                Music(id = R.raw.jungle)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mute = true
    }

}


