package fr.iutlens.mmi.demo


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.game.sprite.get
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.sprite.toSprite
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.utils.Music
import fr.iutlens.mmi.demo.utils.SpriteSheet
import fr.iutlens.mmi.demo.utils.loadSpritesheet

fun makeGameC(): Game {
    val map = """
            1222232222225
            677778777777A
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            BCCCCCCCCCCCG
            122DE222DE225
            677IJ777IJ77A
        """.trimIndent().toMutableTileMap(
              "12345" +
                   "6789A" +
                   "BCDEF" +
                   "GHIJK")
    val tileMap = R.drawable.decor.tiledArea(map)

    val list = mutableSpriteListOf() // Notre liste de sprites
    repeat(7){ // On crée plusieurs sprites aléatoires
        list.add(
            R.drawable.perso.toSprite(
            (tileMap.sizeX*Math.random()*tileMap.w).toFloat(),
            (tileMap.sizeY*Math.random()*tileMap.h).toFloat(),
            (0..2).random()){
                y += 5
                if (y > tileMap.boundingBox.bottom) y = 0f
            }
        )
    }

    val game = Game(background = tileMap,
        spriteList = list,
        map = tileMap,
        transform = FitTransform(tileMap)
    ) { (x,y)->
        val target  =  list[x,y]
        if (target != null) {
            list.remove(target)
            Music.playSound(R.raw.message)
        }
    }

    game.animationDelayMs = 20
    game.update ={
        it.spriteList.update()
        it.invalidate()
    }

    return game
}


@Preview(showBackground = true)
@Composable
fun GameCPreview() {
    LocalContext.current.loadSpritesheet(R.drawable.decor, 5, 4)
    LocalContext.current.loadSpritesheet(R.drawable.perso, 3, 1)
    val game = makeGameC()
    MyApplicationTheme {
        game.View(modifier = Modifier.fillMaxSize())
    }
}