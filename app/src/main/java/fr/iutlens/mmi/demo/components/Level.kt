package fr.iutlens.mmi.demo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.demo.game.Game

@Composable
fun Level(game: Game){
    game.View(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black))
    game.Ath()
    game.Item()
    game.Menu()
}