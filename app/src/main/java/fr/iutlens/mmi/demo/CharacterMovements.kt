package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TileMap
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.sprites.Character
import fr.iutlens.mmi.demo.game.sprite.tiledArea
import fr.iutlens.mmi.demo.game.sprite.toMutableTileMap
import fr.iutlens.mmi.demo.game.transform.FitTransform
import fr.iutlens.mmi.demo.game.transform.FocusTransform
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.loadSpritesheet

fun testCharacter(): Game {
    val map = """
        0122222O3333345
        CGTTTTTTTTTTTHF
        IS!!!!!!!!!!!ZL
        QS!!!!!!!!!!!ZR
        ES!!!!!!!!!!!ZD
        KMYYYYYYYYYYYNJ
        6788888P99999AB
    """.trimIndent().toMutableTileMap(
        "012345"+
                "6789AB"+
                "CDEFGH" +
                "IJKLMN" +
                "OPQRST" +
                "UVWXYZ" +
                "!-*/=_")
    val tileMap = R.drawable.decor.tiledArea(map)
    val staticSprite = BasicSprite(R.drawable.transparent, 1f*((tileMap.w*tileMap.sizeX)/2),1f*((tileMap.h*tileMap.sizeY)/2))
    val game = Game(
        background = tileMap,
        map = tileMap,
        spriteList = mutableSpriteListOf(staticSprite),
        transform = FitTransform(tileMap),
    )
    game.setupControllableCharacter()
    return game
}
@Preview(
    showBackground = true,
    widthDp = 815,
    heightDp = 375
)
@Composable
fun GetCharacterPreview(){
    LocalContext.current.loadSpritesheet(R.drawable.isaac, 3, 4)
    LocalContext.current.loadSpritesheet(R.drawable.decor,6,7)
    val game = testCharacter()
    MyApplicationTheme {
        game.View(modifier= Modifier.fillMaxSize())
    }
}