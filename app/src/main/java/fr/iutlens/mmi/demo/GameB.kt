package fr.iutlens.mmi.demo


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.Sprite
import fr.iutlens.mmi.demo.game.sprite.get
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.utils.SpriteSheet
import fr.iutlens.mmi.demo.utils.loadSpritesheet

fun makeGameB(): Game {
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
   // val sprite = BasicSprite(R.drawable.car,3.5f*tileMap.w,2.5f*tileMap.h)
    val list = mutableSpriteListOf() // Notre liste de sprites
    repeat(7){ // On crée plusieurs sprites aléatoires
        list.add(
            BasicSprite(
                R.drawable.perso,
            (tileMap.sizeX*Math.random()*tileMap.w).toFloat(),
            (tileMap.sizeY*Math.random()*tileMap.h).toFloat(),
            (0..2).random())
        )
    }


    val game = Game(background = tileMap,
        spriteList = list,
        map = tileMap,
        transform = FitTransform(tileMap)
    )

    var current : Sprite? = null

    game.onDragStart = { (x,y) ->
        current = list[x, y]
    }

    game.onDragMove = { (x,y)->
        (current as? BasicSprite)?.let {
            it.x = x
            it.y = y
            game.invalidate()
        }
    }

    return game
}


@Preview(showBackground = true)
@Composable
fun GameBPreview() {
    LocalContext.current.loadSpritesheet(R.drawable.decor, 5, 4)
    LocalContext.current.loadSpritesheet(R.drawable.perso, 3, 1)
    val game = makeGameB()
    MyApplicationTheme {
        game.View(modifier = Modifier.fillMaxSize())
    }
}