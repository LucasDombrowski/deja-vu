package fr.iutlens.mmi.demo

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.game.Game
import fr.iutlens.mmi.demo.game.gameplayResources.Chest
import fr.iutlens.mmi.demo.game.gameplayResources.items.DragonFire
import fr.iutlens.mmi.demo.game.gameplayResources.items.LessFireRateLessDamages
import fr.iutlens.mmi.demo.game.gameplayResources.items.MoreDamagesMoreRate
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaBoots
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaScarf
import fr.iutlens.mmi.demo.game.gameplayResources.items.NinjaShuriken
import fr.iutlens.mmi.demo.game.map.Map
import fr.iutlens.mmi.demo.game.map.Room
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.mutableSpriteListOf
import fr.iutlens.mmi.demo.game.sprite.sprites.characters.Buddy
import fr.iutlens.mmi.demo.ui.theme.MyApplicationTheme
import fr.iutlens.mmi.demo.utils.loadSpritesheet

fun testCharacter(): Game {
    val game = Game(
        map = Map(5..7,R.drawable.decor)
    )
    game.initiate()
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