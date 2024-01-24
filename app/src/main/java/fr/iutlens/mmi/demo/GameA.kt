package fr.iutlens.mmi.demo


import android.os.SystemClock.sleep
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.spriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.transform.FocusTransform
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.loadSpritesheet
import kotlin.math.abs

fun makeGameA(): Game {
    val map = """
            ----^------^-----^--^¨--¨-------¨--
            ____H______H_____H__HT__T_______T__
            ...................................
            ...................................
            !---^-I----^-I---^-|..L-¨----!--¨--
            '___H_J____H_J___H_|..L_T____'__T__
            |.....L......L.....|..L......|.....
            |.....L......L.....|..L......|.....
            !--()----()----()--|..L--()----()--
            '__[]____[]____[]__|..L__[]____[]__
            ###################!^¨I############
            ###################'HTJ############
        """.trimIndent().toMutableTileMap(
              "!-^¨I" +
                   "'_HTJ" +
                   "|.() " +
                   "L#[] ")
    val tileMap = R.drawable.decor.tiledArea(map)
    val sprite = BasicSprite(R.drawable.isaac,3.5f*tileMap.w,2f*tileMap.h)

    return Game(background = tileMap,
        map = tileMap,
        spriteList = mutableSpriteListOf(sprite),
        transform = FocusTransform(tileMap,sprite,5)
    ){ (x,y)->
        val dx = x-sprite.x
        val dy = y-sprite.y

        if (abs(dx)>abs(dy)){
            sprite.x += if (dx>0) tileMap.w else -tileMap.w
        } else {
            sprite.y += if (dy>0) tileMap.h else -tileMap.h
        }
        Music.playSound(R.raw.message)
    }
}

@Preview(showBackground = true)
@Composable
fun GameAPreview() {
    LocalContext.current.loadSpritesheet(R.drawable.decor, 5, 4)
    LocalContext.current.loadSpritesheet(R.drawable.isaac, 3, 4)
    val game = makeGameA()
    MyApplicationTheme {
        game.View(modifier = Modifier.fillMaxSize())
    }
}