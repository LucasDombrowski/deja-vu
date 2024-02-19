package fr.iutlens.mmi.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.levels.Ninja
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.loadSpritesheet

fun testCharacter(): Game {
    val game = Ninja()
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