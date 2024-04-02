package fr.iutlens.mmi.dejaVu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.dejaVu.game.Game
import fr.iutlens.mmi.dejaVu.game.levels.Ninja
import fr.iutlens.mmi.dejaVu.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.dejaVu.utils.loadSpritesheet

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